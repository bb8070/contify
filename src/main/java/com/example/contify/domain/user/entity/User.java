package com.example.contify.domain.user.entity;

import com.example.contify.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name="users",
        uniqueConstraints = {
          @UniqueConstraint(name="uk_users_email", columnNames = "email")
        }
)
public class User extends BaseTimeEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long Id;

    @Column(nullable = false, length =100)
    private String email;

    @Column(nullable = false, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=20)
    private UserRole role;

    private User(String email , String name , UserRole role){
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public static User of(String email, String name , UserRole role){
        return new User(email, name , role);
    }

}
