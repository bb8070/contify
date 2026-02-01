package com.example.contify.domain.content.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Tag {

    @GeneratedValue
    @Id
    private Long id;

    private String name;
}
