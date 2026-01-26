package com.example.contify.global.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@Schema(description = "에러코드 정의")
public enum ErrorCode {

    // 공통
    @Schema(description = "요청 값이 유효하지 않음")
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
    @Schema(description = "서버 내부 오류가 발생함")
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류입니다."),
    @Schema(description = "리소스를 찾을 수 없음")
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "대상을 찾을 수 없습니다."),
    @Schema(description = "토큰이 유효하지 않습니다")
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "토큰이 유효하지 않습니다"),
    @Schema(description = "토큰이 만료되었습니다.")
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "EXPIRED_TOKEN", "토큰이 만료되었습니다."),
    @Schema(description = "리프레시 토큰이 재사용되었습니다. 다시 로그인해주세요")
    REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_REUSED", "리프레시 토큰이 재사용되었습니다. 다시 로그인해주세요"),


    // Content
    @Schema(description = "내용을 찾을 수 없음")
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CONTENT_NOT_FOUND", "내용을 찾을 수 없음"),
    // 회원정보
    @Schema(description = "회원정보가 일치하지 않음")
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    @Schema(description = "이메일 중복이 확인됨")
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


}
