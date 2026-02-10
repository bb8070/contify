package com.example.contify.domain.notification.repository;

import com.example.contify.domain.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("select count(n) from Notification n where n.userId = :userId and n.readAt is null")
    long CountUnread(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Notification n set n.readAt = :now where n.id = : notificationId and n.userId = :userId and n.readAt is null
    """)
    int markAsRead(@Param("userId") Long userId,
                   @Param("notificationId") Long notificationId,
                   @Param("now")LocalDateTime now
                   );

    @Modifying(clearAutomatically = true , flushAutomatically = true)
    @Query("""
        update Notification n set n.readAt = :now where n.userId = :userId and n.readAt is null
        """)
    int markAllAsRead(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
