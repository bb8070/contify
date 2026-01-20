package com.example.contify.domain.member.service;

import com.example.contify.global.error.ErrorCode;
import com.example.contify.global.exception.ApiException;
import com.example.contify.domain.member.entity.Member;
import com.example.contify.domain.member.dto.MemberRequest;
import com.example.contify.domain.member.dto.MemberResponse;
import com.example.contify.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private  final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberResponse getMember (Long id){
        /* * readOnly=true 효과
         * Dirty Checking 비활성화
         * 스냅샷 저장 안 함 -엔터티가 영속성 컨텍스트에 들어올 때의 상태를 몰래 복사해둔 원본상태
         * flush 안 함 - 바뀐 상태를 DB 에 반영 flush후에도 롤백이 가능함
         * ➡️ 조회 성능 + 안정성 증가
         * */
        Member member = memberRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponse(member.getId(), member.getEmail() , member.getName());
    }

    public Long create(MemberRequest memberRequest){
        //Spring Data JPA의 save()는 내부적으로 @Transactional이 걸려 있음
        Member member = new Member (memberRequest.getEmail(), memberRequest.getName());
        return memberRepository.save(member).getId();
    }

    //dirty checking이 중요...
    public void update(Long id ,MemberRequest memberRequest){
        //update는 트랜젝셔널 선언해준다.
        Member member = memberRepository.findById(id).orElseThrow(()-> new ApiException (ErrorCode.MEMBER_NOT_FOUND));
        //DirtyChecking으로 자동 update 되기 때문에 save로 저장하지 않음
        member.changeName(memberRequest.getName());
    }

    public void delete(Long id){
        //delete도 repository레벨에서 transactional이 선언되어있음 - 그렇지만 선언해줘야함...
        //데이터 삭제는 정합성이 중요하기 때문에 service차원에서 트랜잭션 관리
        memberRepository.deleteById(id);
    }
}
