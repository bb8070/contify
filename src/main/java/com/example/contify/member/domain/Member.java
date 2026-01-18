package com.example.contify.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor
public class Member {
        @Id
        @GeneratedValue
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
