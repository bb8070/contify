package com.example.contify.domain.content.entity;

import com.example.contify.domain.user.entity.User;
import com.example.contify.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name="contents",
        indexes = {
                @Index(name="idx_content_category", columnList = "category"),
                @Index(name="idx_content_created_at", columnList = "created_at")
        }
)
public class Content extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //이렇게 하면 자동으로 테이블 생성시 id값이 auto_increment로 들어감~
    @Column(name="id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob // ContainsIgnoreCase가 동작하지 않음
    @Column(nullable = false)
    private String body;

    @Column(nullable = false, name="view_count")
    private long viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=50)
    private ContentCategory category;

    //FetchType.LAZY : 필요할 때만 조회
    //ManyToOne = DB외래키 + 객체참조
    @ManyToOne(fetch= FetchType.LAZY , optional = false)
    @JoinColumn(
            name="created_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name="fk_contents_created_user")
    )
    private User createdBy;

    private Content(String title, String body, ContentCategory category, User createdBy){
        this.title = title;
        this.body = body;
        this.category =category;
        this.createdBy =createdBy;
    }

    public Content of(String title, String body, ContentCategory category, User createdBy){
        return new Content(title, body, category, createdBy);
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

}
