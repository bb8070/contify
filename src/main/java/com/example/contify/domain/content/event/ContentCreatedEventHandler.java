package com.example.contify.domain.content.event;

import com.example.contify.domain.follow.repository.FollowRepository;
import com.example.contify.domain.notification.entity.Notification;
import com.example.contify.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContentCreatedEventHandler {
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;

    @Async("appTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onContentCreated(ContentCreateEvent e){
        List<Long> followerIds =  followRepository.findFollowerIdsByAuthorId(e.authorId());
        List<Notification> notis = followerIds.stream()
                .map(fid -> Notification.newContent(fid, e.contentId(), e.authorId()))
                .toList();
        notificationRepository.saveAll(notis);
    }

}
