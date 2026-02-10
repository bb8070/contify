package com.example.contify.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="notification", indexes={
    @Index(name="idx_notification_user_created", columnList = "user_id , created_at"),
    @Index(name="idx_notification_user_read", columnList = "user_id, read_at")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false, length = 30)
    private NotificationType type;

    @Column(name="content_id")
    private Long contentId;

    @Column(name="actor_id")
    private Long actorId;

    @Column(name="message" , nullable = false, length=255)
    private String message;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="read_at")
    private LocalDateTime readAt;

    private Notification(Long userId,
                         NotificationType type,
                         Long contentId,
                         Long actorId,
                         String message){
        this.userId = userId;
        this.type = type;
        this.contentId = contentId;
        this.actorId = actorId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public static Notification newContent(Long receiverId , Long contentId, Long authorId){
        return new Notification(
                receiverId,
                NotificationType.NEW_CONTENT,
                contentId,
                authorId,
                "새 글이 등록되었습니다."
        );
    }

    public static Notification liked(Long receiverId, Long contentId, Long actorId){
        return new Notification(
                receiverId,
                NotificationType.CONTENT_LIKED
                ,contentId
                ,actorId
                ,"내 글에 좋아요가 눌렸습니다."
        );
    }

    public boolean isRead(){
        return readAt != null;
    }

    public void markRead(LocalDateTime now){
        if(this.readAt == null){
            this.readAt = now;
        }
    }

}
