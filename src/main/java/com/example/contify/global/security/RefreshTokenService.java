package com.example.contify.global.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.List;


@Service
public class RefreshTokenService
{
    private final StringRedisTemplate redisTemplate;

    public RefreshTokenService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    private String key(Long userId){
        return "refreshToken:"+userId;
    }

    public void save(Long userId , String refreshToken, long ttlMs){
        redisTemplate.opsForValue().set( key(userId), refreshToken, Duration.ofMillis((ttlMs)));
    }

    public Optional<String> find(Long userId){
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(userId)));
    }

    public void delete(Long userId){
        redisTemplate.delete(key(userId));
    }

    public boolean matches(Long userId, String refreshToken){
        return find(userId).map(saved->saved.equals(refreshToken)).orElse(false);
    }

    public boolean rotate(Long userId , String oldToken , String newToken, long ttlMs){
        String k = key(userId);
        if(!matches(userId, oldToken)) return false;
        save(userId, newToken , ttlMs);
        return true;
    }

    public static final DefaultRedisScript<Long> ROTATE_SCRIPT;
    static{
        //KEYS	Redis key 전용 파라미터
        //ARGV	일반 인자(arguments)
        ROTATE_SCRIPT =  new DefaultRedisScript<>();
        ROTATE_SCRIPT.setResultType(Long.class);
        /*
        * GET refreshToken:1
        * if value == oldToken then
        * SET refreshToken:1 = newToken (TTL=ttlMs)
        * */
        ROTATE_SCRIPT.setScriptText(
                "local current = redis.call('GET', KEYS[1]) \n " +
                        "if not current then return 0 end \n" +
                        "if current == ARGV[1] then \n" +
                        "redis.call('PSETEX', KEYS[1], ARGV[3], ARGV[2]) \n"+
                        "return 1 \n"+
                        "end \n"+
                        "return 0"
        );
    }

    public boolean rotateAtomically(Long userId, String oldToken, String newToken, long ttlms){
        String redisKey = key(userId);
        Long result = redisTemplate.execute(
                ROTATE_SCRIPT,
                List.of(redisKey),//KEYS
                oldToken,//ARGV[1]
                newToken,//ARGV[2]
                String.valueOf(ttlms)//ARGV[3]
        );
        return result !=null && result == 1L;
    }
}
