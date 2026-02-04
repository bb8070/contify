package com.example.contify.content;

import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.content.service.ContentViewService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = "spring.sql.init.mode=never")
@Sql(scripts = "/test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ContentViewConcurrencyTest {

    @Autowired
    ContentRepository contentRepository;
    @Autowired
    ContentViewService contentViewService;

    private  Long contentId = 1L;

    @Test
    void dirtyChecking_lostUpdate_canHappen() throws Exception{
        int threads = 50;
        runConcurrently(threads, (i)->{
            contentViewService.increaseViewDirtyChecking((long) i , contentId);
        });

        Content loaded = contentRepository.findById(contentId).orElseThrow();
        System.out.println("dirty result viewCount="+loaded.getViewCount());

        assertThat(loaded.getViewCount()).isLessThanOrEqualTo(threads);

    }

    @Test
    void atomicUpdate_isSafe() throws Exception{
        int threads=50;
        runConcurrently(threads, (i)->{
            contentViewService.increaseViewAtomic((long) i , contentId);
        });

        Content loaded = contentRepository.findById(contentId).orElseThrow();
        assertThat(loaded.getViewCount()).isEqualTo(threads);
    }

    @Test
    void optimisticLock_withRetry_isSafe_enough() throws Exception{
        int threads = 50;
        runConcurrently(threads, (i)->{
            contentViewService.increaseViewOptimisticRetry((long) i, contentId);
        });

        Content loaded = contentRepository.findById(contentId).orElseThrow();
        assertThat(loaded.getViewCount()).isEqualTo((long) threads);
    }

    private void runConcurrently(int threads, ThrowingIntConsumer task) throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for(int i=0; i<threads; i++){
            final int idx = i;
            executor.submit(()->{
                ready.countDown();
                try {
                    start.await();
                    task.accept(idx);
                }catch (Exception e){
                    e.printStackTrace();
                }finally{
                    done.countDown();
                }
            });
        }
        ready.await();
        start.countDown();
        done.await();

        executor.shutdown();

    }
}
@FunctionalInterface
interface ThrowingIntConsumer{

    void accept(int value) throws Exception;
}
