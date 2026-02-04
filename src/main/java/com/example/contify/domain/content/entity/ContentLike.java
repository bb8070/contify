package com.example.contify.domain.content.entity;

import jakarta.persistence.*;

//중복방지
@Entity
@Table(
        name="content_like",
        uniqueConstraints = @UniqueConstraint(
                name="uk_like_user_content",
                columnNames = {"user_id", "content_id"}
        )
)
public class ContentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="content_id", nullable = false)
    private Content content;

    protected ContentLike(){}

    public ContentLike(Long userId, Content content){
        this.userId = userId;
        this.content = content;
    }

}
