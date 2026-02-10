package com.example.contify.domain.content.event;

import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.entity.LikeLog;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.content.repository.ContentViewRedisRepository;
import com.example.contify.domain.content.repository.LikeLogRepository;
import com.example.contify.domain.notification.entity.Notification;
import com.example.contify.domain.notification.repository.NotificationRepository;
import com.example.contify.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContentLikedEventHandler {

    private final LikeLogRepository likeLogRepository;
    private final ContentViewRedisRepository rankRedisRepository;
    private final NotificationRepository notificationRepository;
    private final ContentRepository contentRepository;

    @Async("appTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLiked(ContentLikedEvent e){
        likeLogRepository.save(new LikeLog(e.userId(), e.contentId()));
        rankRedisRepository.increase(e.contentId());

        Content content = contentRepository.findById(e.contentId()).orElseThrow();
        Long authorId = content.getCreatedUser().getId();

        if(!authorId.equals(e.userId())){
            notificationRepository.save(Notification.liked(authorId, e.contentId(), e.userId()));
        }

    }

}
