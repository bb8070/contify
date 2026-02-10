package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.dto.ContentListItem;
import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.QContent;
import com.example.contify.domain.content.entity.QContentBookmark;
import com.example.contify.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.contify.domain.content.entity.QContentBookmark.contentBookmark;


@RequiredArgsConstructor
public class ContentBookmarkRepositoryImpl implements ContentBookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<ContentListItem> findBookMarkContentsByUserId(ContentSearchCondition condition, Pageable pageable) {
        QContent c = QContent.content;
        QUser u = QUser.user;
        QContentBookmark b = contentBookmark;

        Long userId = parseUserId(condition);

        BooleanBuilder where = new BooleanBuilder();
        where.and(b.userId.eq(userId));

        List<ContentListItem> contents = queryFactory.select(
                Projections.constructor(ContentListItem.class,
                        c.id,
                        c.title,
                        c.category,
                        c.viewCount,
                        c.createdAt,
                        u.name))
                .from(b)
                .join(b.content, c)
                .join(c.createdUser, u)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(contents, pageable, () -> {
            Long total = queryFactory
                    .select(b.count()) //count쿼리로 전체 건수를 조회함
                    .from(b)
                    .join(b.content, c)
                    .where(where)
                    .fetchOne();
            return total == null ? 0L : total;
        });
    }

    private Long parseUserId(ContentSearchCondition condition){
        return Long.valueOf(condition.getUserId());
    }

    private  BooleanExpression categoryEq(QContent c){
        return null;
    }


}