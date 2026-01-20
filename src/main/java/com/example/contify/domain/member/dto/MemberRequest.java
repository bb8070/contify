package com.example.contify.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberRequest {

    @Email
    @NotBlank
    @Schema(example = "test@test.com")
    private String email;
    @NotBlank
    private String name;
}
