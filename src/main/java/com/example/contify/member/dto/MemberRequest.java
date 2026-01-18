package com.example.contify.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String name;
}
