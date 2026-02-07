package com.example.contify.content;

import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.entity.ContentCategory;
import com.example.contify.domain.content.repository.ContentLikeRepository;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.content.service.ContentLikeService;
import com.example.contify.domain.user.entity.User;
import com.example.contify.domain.user.entity.UserRole;
import com.example.contify.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.sql.init.mode=never")
class ContentLikeConcurrencyTest {

    @Autowired
    ContentRepository contentRepository;
    @Autowired
    ContentLikeRepository likeRepository;
    @Autowired
    ContentLikeService likeService;
    @Autowired
    UserRepository userRepository;

    private  Long contentId;
    private  Long userId;

    @BeforeEach
    void setUp() {
        User user = userRepository.saveAndFlush(
                User.of("writer1@test.com", "작성자1", UserRole.USER) // 너 User 생성자/필드에 맞춰 수정
        );
        userId = user.getId();

        Content content = Content.of(
                "t", "b", ContentCategory.BACKEND, user
        );
        contentId = contentRepository.saveAndFlush(content).getId();
        // 방어 코드 (테스트 안정성)
        assertThat(userId).isNotNull();
        assertThat(contentId).isNotNull();

    }

    @Test
    void concurrent_like_by_many_users_should_increase_likeCount_exactly() throws Exception {
        int threads = 50;//서로다른 50명이 같은 콘텐츠에 동시에 좋아요 요청
        runConcurrently(threads, (i)->{
            long userId=i+1L;
            likeService.like(userId, contentId);
        });
        Content loaded = contentRepository.findById(contentId).orElseThrow();
        long likeRows = likeRepository.countByContentId(contentId);

        assertThat(likeRows).isEqualTo(threads);// 좋아요 테이블의 로우가 50개인지
        assertThat(loaded.getLikeCount()).isEqualTo((long) threads); //콘텐츠의 컬럼값이 50인지

    }

    @Test
    void concurrent_like_by_same_user_should_be_idempotent() throws Exception {
        int threads = 30; //같은 사용자가 30번 스레드로 요청
        long sameUserId = 1L;
        runConcurrently(threads, (i) -> likeService.like(sameUserId, contentId));

        Content loaded = contentRepository.findById(contentId).orElseThrow();
        long likeRows = likeRepository.countByContentId(contentId);

        assertThat(likeRows).isEqualTo(1); // 30개 요청이와도 1번만 반영
        assertThat(loaded.getLikeCount()).isEqualTo(1L); //DB에서 가지고 온 값도 1인지?
    }

    private void runConcurrently(int threads , ThrowingIntConsumer task) throws Exception{

        //threads 개수만큼 스레드 생성
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch ready = new CountDownLatch(threads); //준비 -
        CountDownLatch start = new CountDownLatch(1);//동시에 출발 (동시성)
        CountDownLatch done = new CountDownLatch(threads);//모두끝났는지

        for(int i =0; i< threads; i++){
            final int idx = i;
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
        void accept(int i) throws Exception;
    }

}
