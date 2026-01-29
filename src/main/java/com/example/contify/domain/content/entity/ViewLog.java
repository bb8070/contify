package com.example.contify.domain.content.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="view_log")
@Getter
//엔터티에는 AllArgsConstructor 은 안씀 - JPA에는 id가 직접적으로 들어가면 안됨
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( nullable = false, name="id")
    private Long id;

    private Long contentId;

    @Column( name="viewed_at")
    private LocalDateTime viewedAt;

    public ViewLog(Long contentId){
        this.contentId = contentId;
        this.viewedAt = LocalDateTime.now();
    }

}
