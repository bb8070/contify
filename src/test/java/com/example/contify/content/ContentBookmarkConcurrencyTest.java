package com.example.contify.content;

import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.entity.ContentCategory;
import com.example.contify.domain.content.repository.ContentBookmarkRepository;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.content.service.ContentBookmarkService;
import com.example.contify.domain.user.entity.User;
import com.example.contify.domain.user.entity.UserRole;
import com.example.contify.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.stream;

@SpringBootTest(properties = "spring.sql.init.mode=never")
@Sql(scripts="/test-setup.sql", executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ContentBookmarkConcurrencyTest {

    @Autowired
    ContentRepository contentRepository;
    @Autowired
    ContentBookmarkRepository bookmarkRepository;
    @Autowired
    ContentBookmarkService bookmarkService;
    @Autowired
    UserRepository userrepository;

    private Long contentId;
    private Long userId;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        User user = userrepository.saveAndFlush(
                User.of("writer1@test.com", "작성자", UserRole.USER)
        );
        userId = user.getId();
        Content content = Content.of("t","b", ContentCategory.BACKEND, user);
        contentId= contentRepository.saveAndFlush(content).getId();

        assertThat(userId).isNotNull();
        assertThat(contentId).isNotNull();

    }

    @Test
    void concurrent_bookmark_by_many_users_should_increase_bookmarkCount_extractly() throws Exception {
        int threads=50;

        List<Long> userIds = new ArrayList<>();
        for(int i=0; i<threads; i++){
            User u = userRepository.saveAndFlush(
                    User.of("u"+i+"@test.com", "u"+i, UserRole.USER)
            );
            userIds.add(u.getId());
        }

        runConcurrently(threads, (i)->{
            bookmarkService.marked(userIds.get(i), contentId);
        });

        Content loaded = contentRepository.findById(contentId).orElseThrow();
        long bookmarkRows = bookmarkRepository.countByContent_Id(contentId);

        assertThat(bookmarkRows).isEqualTo(threads);

        assertThat(loaded.getBookmarkCount()).isEqualTo(threads);

    }
    @Test
    void concurrent_bookmark_by_same_user_should_be_idempotent() throws Exception {
        int threads = 30;
        long sameUserId = userId;

        runConcurrently(threads, (i)-> bookmarkService.marked(sameUserId, contentId));

        Content loaded = contentRepository.findById(contentId).orElseThrow();
        long bookmarkRows = bookmarkRepository.countByContent_Id(contentId);

        assertThat(bookmarkRows).isEqualTo(1);
        assertThat(loaded.getBookmarkCount()).isEqualTo(1);

    }

    private void runConcurrently(int threads, ThrowingIntConsumer task) throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for(int i = 0 ; i< threads; i++){
            final  int  idx = i;
            executor.submit(()->{
                ready.countDown();
                try {
                    start.await();
                    task.accept(idx);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();


    }

    @FunctionalInterface
    interface ThrowingIntConsumer{
        void accept(int i ) throws  Exception;
    }

}
