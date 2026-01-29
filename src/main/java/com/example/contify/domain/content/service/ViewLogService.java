package com.example.contify.domain.content.service;

import com.example.contify.domain.content.entity.ViewLog;
import com.example.contify.domain.content.repository.ViewLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor //정석
public class ViewLogService {

    private final ViewLogRepository viewLogRepository;

    //기존 트랜잭션이 있어도 새로운 트랜잭션을 만들어서 실행
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLogRequiresNew(Long contentId){

        viewLogRepository.save(new ViewLog(contentId));
    }
}
