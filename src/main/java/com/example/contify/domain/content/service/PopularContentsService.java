package com.example.contify.domain.content.service;

import com.example.contify.domain.content.dto.PopularContentsResponse;
import com.example.contify.domain.content.dto.PopularMetric;
import com.example.contify.domain.content.dto.PopularPeriod;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.redis.PopularKeyFactory;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.content.repository.PopularRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PopularContentsService {
    private final PopularRedisRepository popularRedisRepository;
    private final ContentRepository contentRepository;

    /*
    * 인기 콘텐츠 Top N 조회
    * 1. Redis ZSET에서 Top N 콘텐츠 ID 조회
    * 2. DB에서 콘텐츠 상세 정보 조회
    * 3. Redis 점수 (score)와 콘텐츠 정보 결합
    * */
    @Transactional(readOnly = true)
    public PopularContentsResponse getPopular(PopularPeriod period , PopularMetric metric , int limit , String category){
        LocalDateTime  now = LocalDateTime.now();
        String rankingKey = PopularKeyFactory.rankingKey(period, metric, now);

        //Redis에서 TopN + score 조회
        LinkedHashMap<Long, Long> top = popularRedisRepository.getTopNWithScore(rankingKey, limit);
        if(top.isEmpty()){
            return new PopularContentsResponse(period , metric, List.of());
        }
        List<Long> idsInOrder = new ArrayList<>(top.keySet());

        //DB에서 콘텐츠 정보 조회
        List<Content> contents = contentRepository.findByIdIn(idsInOrder);

        Map<Long , Content> byId = contents.stream().collect(Collectors.toMap(Content::getId, Function.identity()));

        //Redis 순서대로 응답 조립
        List<PopularContentsResponse.Item> items = new ArrayList<>();
        for(Long id : idsInOrder){
            Content c = byId.get(id);
            if(c==null) continue;
            //(선택) 카테고리 필터
            if(category != null && !category.isBlank()){
                String cCat = String.valueOf(c.getCategory());
                if(!category.equalsIgnoreCase(cCat)) continue;
            }
            items.add(new PopularContentsResponse.Item(
                    c.getId()
                    ,c.getTitle()
                    ,String.valueOf(c.getCategory())
                    ,top.getOrDefault(id , 0L)
            ));
        }

        return new PopularContentsResponse(period , metric , items);

    }


}
