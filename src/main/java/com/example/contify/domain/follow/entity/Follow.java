package com.example.contify.domain.follow.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "follow" ,
        uniqueConstraints = @UniqueConstraint(name = "uk_follow_author_follower", columnNames = {"author_id", "follower_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="author_id", nullable = false)
    private  Long authorId;

    @Column(name="follower_id" , nullable =false)
    private Long followerId;

    @Column(name="created_at" , nullable = false)
    private LocalDateTime createdAt;

    private Follow(Long authorId, Long followerId){
        this.authorId = authorId;
        this.followerId = followerId;
        this.createdAt = LocalDateTime.now();
    }

    public static Follow of(Long authorId , Long followerId)
    {
        return new Follow(authorId, followerId);
    }
}
