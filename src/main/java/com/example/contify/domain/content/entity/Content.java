package com.example.contify.domain.content.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name="contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //이렇게 하면 자동으로 테이블 생성시 id값이 auto_increment로 들어감~
    @Column(name="id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(nullable = false, length=50)
    private String category;

    @Column(nullable = false, name="view_count")
    private long viewCount = 0;

    @Column(nullable = false, name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    private Content(String title, String body, String category){
        this.title = title;
        this.body = body;
        this.category =category;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

}
