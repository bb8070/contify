package com.example.contify.domain.content.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class PopularRedisRepository {

    private final StringRedisTemplate redis;

    public void increase(String rankingKey , String deltaKey , long contentId , long delta){
        String member =  String.valueOf(contentId);
        redis.opsForZSet().incrementScore(rankingKey, member, delta); //Zset 점수 증가
        redis.opsForHash().increment(deltaKey, member, delta); // 변경분 기록
    }

    public List<Long> getTopN(String rankingKey , int limit){
        Set<String> members = redis.opsForZSet().reverseRange(rankingKey, 0, limit-1);
        if(members==null || members.isEmpty()) return List.of();

        List<Long> ids = new ArrayList<>(members.size());
        for(String m : members) ids.add(Long.parseLong(m));
        return ids;
    }

    public LinkedHashMap<Long, Long> getTopNWithScore(String rankingKey, int limit){
        var tuples = redis.opsForZSet().reverseRangeWithScores(rankingKey , 0 , limit-1);
        if(tuples==null|| tuples.isEmpty())  return new LinkedHashMap<>();
        LinkedHashMap<Long, Long> result = new LinkedHashMap<>();
        for(var t: tuples){
            if(t.getValue()==null) continue;
            long id = Long.parseLong(t.getValue());
            long score = (t.getScore() ==null)? 0L : t.getScore().longValue();
            result.put(id, score);
        }
        return result;
    }

    public Map<Long,Long> readAllDeltas(String deltaKey){
        Map<Object, Object> entries = redis.opsForHash().entries(deltaKey);
        if(entries==null || entries.isEmpty()) return Map.of();

        Map<Long, Long> deltas = new HashMap<>(entries.size());
        for(Map.Entry<Object, Object> e : entries.entrySet()){
            Long id = Long.parseLong(String.valueOf(e.getKey()));
            Long delta = Long.parseLong(String.valueOf(e.getValue()));
            if(delta!=null && delta>0) deltas.put(id , delta);
        }

        return deltas;

    }

    public void clearDelta(String deltaKey , long contentId){
        redis.opsForHash().delete(deltaKey, String.valueOf(contentId));
    }

}
