package com.example.contify.domain.content.entity;

import com.example.contify.domain.user.entity.User;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(
        name="content_bookmark",
        uniqueConstraints = @UniqueConstraint(
                name="uk_bookmark_user_content",
                columnNames = {"user_id", "content_id"}
        )
)
public class ContentBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(
            name = "content_id",
            nullable = false
    )
    private Content content;

    @Column(name="created_at")
    private LocalDateTime createdAt;
    protected ContentBookmark() {

    }
    public ContentBookmark(Long userId, Content content){
        this.userId =userId;
        this.content=content;
        this.createdAt=LocalDateTime.now();
    }

}
