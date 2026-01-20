package com.example.contify.domain.member.controller;

import com.example.contify.global.response.ApiResponse;
import com.example.contify.domain.member.dto.MemberRequest;
import com.example.contify.domain.member.dto.MemberResponse;
import com.example.contify.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원 찾기" , description = "id로 회원을 조회합니다")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> get (@PathVariable Long id){
        return ResponseEntity.ok(memberService.getMember(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(@RequestBody MemberRequest request){
        Long id = memberService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody MemberRequest request
    ){
        memberService.update(id, request);
        /*ResponseEntity.noContent().build()는 **REST API에서 “요청은 성공했지만, 응답 바디는 없다”**를 명확히 표현하는 방법*/
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
