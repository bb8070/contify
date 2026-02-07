package com.example.contify.domain.content.service;

import com.example.contify.domain.content.dto.ContentListItem;
import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.entity.ContentBookmark;
import com.example.contify.domain.content.repository.ContentBookmarkRepository;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.user.repository.UserRepository;
import com.example.contify.global.error.ErrorCode;
import com.example.contify.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentBookmarkService {

    private final ContentBookmarkRepository bookmarkRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void marked(Long userId, Long contentId){
        Content content = contentRepository.findById(contentId).orElseThrow(()-> new IllegalArgumentException("content not found"));
        try{
            bookmarkRepository.save(new ContentBookmark(userId, content));//유니크로 중복 방지
        }catch (DataIntegrityViolationException e){
            // 이미 좋아요 한 상태 - 멱등 처리 (조용히 성공으로 봄)
            return;
        }
        contentRepository.increaseBookmark(contentId); //원자적 증가
    }

    @Transactional
    public void unmarked(Long userId, Long contentId){
        long delete =  bookmarkRepository.deleteByUserIdAndContent_Id(userId, contentId);//유니크로 중복 방지
        if(delete==0) return;
        contentRepository.decreaseBookmark(contentId); //원자적 증가
    }

    @Transactional(readOnly = true)
    public Page<ContentListItem> getContent(ContentSearchCondition condition, Pageable pageable) {
        if(condition==null|| condition.getUserId() ==null){
            new ApiException(ErrorCode.INVALID_REQUEST);
        }
        return bookmarkRepository.findBookMarkContentsByUserId(condition, pageable);

    }
}
