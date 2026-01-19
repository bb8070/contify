package com.example.contify.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(name ="ApiResponse", description = "공통 API 응답 포맷")
public class ApiResponse<T> {
    @Schema(description = "성공여부", example="true")
    private boolean success;
    @Schema(description = "응답코드", example="SUCCESS")
    private String code;
    @Schema(description = "메세지", example ="요청이 성공했습니다")
    private String message;
    @Schema(description = "실제데이터는 제너릭")
    private T data; //T는 어떤 데이터도 담을 수 있다

    private ApiResponse(boolean success, String code , String message, T data){
        this.success=success;
        this.code=code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(true,"SUCCESS", "요청이 성공했습니다", data);
    }

    public static ApiResponse<Void> error(String code, String message){
        return new ApiResponse<>(false, code, message, null);
    }

    public static ApiResponse<Void> success(){
        return new ApiResponse<>(true,"SUCCESS","요청이 성공했습니다", null);
    }

}
