package com.example.contify.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {
    //QueryDSL 쿼리를 만들 때 중심이 되는 객체가 JPAQueryFactory.
    //매번 new JPAQueryFactory(em) 만들지 말고, 스프링이 Bean으로 관리하게 함.
    //private final JPAQueryFactory queryFactory; 로 주입받아 바로씀
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em){
        return new JPAQueryFactory(em);
    }
}
