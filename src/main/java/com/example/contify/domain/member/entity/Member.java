package com.example.contify.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) //자동생성
        private Long id;
        private String email;
        private String name;

        public Member (String email , String name){
            this.email = email;
            this.name = name;
        }
        public void changeName(String name){
            this.name = name;
        }

}
