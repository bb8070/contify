package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.entity.QContent;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.springframework.data.jpa.repository.query.JpqlQueryBuilder.orderBy;

@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private final JPAQueryFactory queryFactory; //global 하위에 있음

    @Override
    public Page<Content> search(ContentSearchCondition condition, Pageable pageable) {

        QContent content = QContent.content;

        List<Content> contents = queryFactory
            .selectFrom(content) // content테이블에서 조회
            .where(
                    keywordContains(condition.getKeyword()),
                    categoryEq(condition.getCategory())) // 조건을 동적으로 조립
            .offset(pageable.getOffset()) //페이징
            .limit(pageable.getPageSize()) //페이징
            .orderBy(orderBy(pageable, content)) //정렬
            .fetch(); // 리스트 가지고 오기

        Long total = queryFactory
            .select(content.count()) //count쿼리로 전체 건수를 조회함
            .from(content)
            .where(keywordContains(condition.getKeyword()),
                    categoryEq(condition.getCategory()))
            .fetchOne();

        //total을 만들지 않으면 Page가 이상해지는 경우가 있음.
        return new PageImpl<>(contents, pageable, total == null ? 0: total);

    }

    private BooleanExpression keywordContains(String keyword){
        if(!StringUtils.hasText(keyword)) return null; //null을 반환하는 이유 : where(null)을 무시해서 조건이 있을 때만 붙게함
        QContent content = QContent.content;
        return content.title.containsIgnoreCase(keyword)
                .or(content.body.contains(keyword));
    }

    private BooleanExpression categoryEq(String category){
        if(!StringUtils.hasText((category))) return null;
        return QContent.content.category.eq(category);
    }

    //정렬 propertiy는 entity의 필드명과 맞춰야함.
    private OrderSpecifier<?> orderBy(Pageable pageable , QContent content){
        if(pageable.getSort().isEmpty()){
            return content.createdAt.desc();
        }
        Sort.Order order = pageable.getSort().iterator().next();
        boolean asc = order.isAscending();
        
        return switch (order.getProperty()){
            case "viewCount" -> asc ? content.viewCount.asc() : content.viewCount.desc();
            case "createdAt" -> asc ? content.createdAt.asc() : content.createdAt.desc();
            default -> content.createdAt.desc();
        };
    }

}

