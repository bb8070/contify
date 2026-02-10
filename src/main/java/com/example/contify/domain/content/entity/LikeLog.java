package com.example.contify.domain.content.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="like_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name="id")
    private Long id;

    private Long contentId;

    private Long userId;

    @Column(name="liked_at")
    private LocalDateTime likedAt;

    public LikeLog(Long userId , Long contentId){
        this.userId = userId;
        this.contentId= contentId;
        this.likedAt= LocalDateTime.now();
    }

}
