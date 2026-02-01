package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.dto.ContentListItem;
import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.entity.ContentCategory;
import com.example.contify.domain.content.entity.QContent;
import com.example.contify.domain.content.entity.QContentTag;
import com.example.contify.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.contify.domain.content.entity.QContent.content;
import static org.springframework.data.jpa.repository.query.JpqlQueryBuilder.orderBy;

@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private final JPAQueryFactory queryFactory; //global 하위에 있음

    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt","viewCount","title");

    @Override
    public Page<ContentListItem> search(ContentSearchCondition condition, Pageable pageable) {

        QContent c = QContent.content;
        QUser u = QUser.user;

        //조건이 늘어나도 구조가 무너지지 않기 때문에.
        BooleanBuilder where = buildWhere(condition, c);

        List<ContentListItem> contents = queryFactory
            .select(Projections.constructor(
                    ContentListItem.class,
                    c.id,
                    c.title,
                    c.category,
                    c.viewCount,
                    c.createdAt,
                    u.name
            ))
            .from(c)// content테이블에서 조회
            .join(c.createdBy, u)
            .where(where) // 조건을 동적으로 조립
            .offset(pageable.getOffset()) //페이징 -DB에서 필요한 만큼만 조회
            .limit(pageable.getPageSize()) //페이징 - DB에서 필요한 만큼만 조회
            .orderBy(toOrderSpecifiers(pageable.getSort(), c)) //정렬
            .fetch(); // 리스트 가지고 오기

        //매번 카운트를 가지고 오지않고 카운트가 변경될때나, 호출될땜나 가지고 온다
        /**
         * 조회 결과가 pageSize보다 작으면 → 마지막 페이지임이 확정 → total을 추론 가능
         * 또는 첫 페이지에서 데이터가 적으면 → total이 작음
         * */
        return PageableExecutionUtils.getPage(contents, pageable, ()->{
            Long total = queryFactory
                    .select(c.count()) //count쿼리로 전체 건수를 조회함
                    .from(c)
                    .where(where)
                    .fetchOne();
            return total == null ? 0L: total;
        });

    }
    @Override
    public Page<ContentListItem> findContents(ContentSearchCondition condition, Pageable pageable){

        QContent c = QContent.content;

        QUser u = QUser.user;

        BooleanBuilder where = buildWhere(condition, c);

        List<ContentListItem> contents = queryFactory.select(Projections.constructor(ContentListItem.class,
                                                        c.id , c.title , c.category, c.viewCount , c.createdAt , u.name))
                .from(c)
                .where(where)
                .join(c.createdBy , u)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderBy(pageable, c))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(c.count()).from(c).where(where);
        return PageableExecutionUtils.getPage(
                contents,
                pageable,
                ()->{
                    Long total = countQuery.fetchOne();
                    return total == null ? 0L : total;
                }
        );
    }

    @Override
    public Optional<Content> findDetailById(Long id){
        QContent c = QContent.content;
        QUser u = QUser.user;
        return Optional.ofNullable(
                queryFactory.selectFrom(c)
                        .join(c.createdBy , u).fetchJoin()
                        .where(c.id.eq(id))
                        .fetchOne()
        );
    }

    //total을 구하지 않는 Slice가 total을 구하는 Page보다 합리적임 : 이유 . hasNext만을 판별하기 때문에 pageSize+1만 조회함.
    @Override
    public Slice<ContentListItem> searchSlice(ContentSearchCondition cond, Pageable pageable) {
        QContent c = QContent.content;
        QUser u = QUser.user;

        List<ContentListItem> items = queryFactory
                .select(Projections.constructor(
                                ContentListItem.class,
                                c.id, c.title, c.category, c.viewCount, c.createdAt, u.name))
                .from(c)
                .join(c.createdBy , u)
                .where(buildWhere(cond, c))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) //한 개 더
                .fetch();

        boolean hasNext = items.size() > pageable.getPageSize();
        if (hasNext) items.remove(items.size() - 1);

        return new SliceImpl<>(items, pageable, hasNext);
    }

    private BooleanBuilder buildWhere(ContentSearchCondition condition, QContent c){
        BooleanBuilder builder = new BooleanBuilder(); //조건이 있으면 그냥 and로 붙여주면 된다.
        if(condition == null) return builder;

        if(StringUtils.hasText(condition.getKeyword())){
            String kw = condition.getKeyword();
            builder.and(keywordContains(kw));
            //body는 clob이라 contains
//            builder.and(c.title.containsIgnoreCase(kw).or(c.body.contains(kw)));
        }
        if(condition.getCategory() != null){
//            builder.and(c.category.eq(condition.getCategory()));
            builder.and(categoryEq(condition.getCategory()));
        }

        if(condition.getFrom() != null){
            builder.and(createdAtGoe(condition.getFrom()));
        }

        if(condition.getTo()!=null){
            builder.and(createdAtLt(condition.getTo()));
        }

        if(condition.getTagIds()!=null && !condition.getTagIds().isEmpty()){
            builder.and(hasAnyTags(condition.getTagIds(), c));
        }

        return builder;
    }



private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort, QContent c){
        if(sort==null||sort.isUnsorted()){
            return new OrderSpecifier[]{c.createdAt.desc()};
        }
        PathBuilder<?> entityPath = new PathBuilder<>(c.getType(), c.getMetadata());
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for(Sort.Order o : sort){
            String property = o.getProperty();
            if(!ALLOWED_SORTS.contains(property)) continue;
            Order direction =o.isAscending() ? Order.ASC : Order.DESC;
            orders.add(new OrderSpecifier(direction, entityPath.get(property)));

        }

        if(orders.isEmpty()){
            return new OrderSpecifier[]{c.createdAt.desc()};
        }

        return orders.toArray(new OrderSpecifier[0]);

    }

    private BooleanExpression keywordContains(String keyword){
        if(!StringUtils.hasText(keyword)) return null; //null을 반환하는 이유 : where(null)을 무시해서 조건이 있을 때만 붙게함

        return content.title.containsIgnoreCase(keyword)
                .or(content.body.contains(keyword));
    }

    private BooleanExpression categoryEq(ContentCategory category){
        return category != null ? content.category.eq(category) : null;
    }

    private BooleanExpression createdAtGoe(LocalDate from){
        if(from==null) return null;
        return content.createdAt.goe(from.atStartOfDay());
    }

    private BooleanExpression createdAtLt(LocalDate to){
        if(to==null) return null;
        return content.createdAt.lt(to.plusDays(1).atStartOfDay());
    }

    private BooleanExpression hasAnyTags(List<Long> tagIds, QContent c){
        if(tagIds == null || tagIds.isEmpty()) return null;
        QContentTag ct = QContentTag.contentTag;
        return JPAExpressions.selectOne().from(ct).where(ct.content.eq(c), ct.tag.id.in(tagIds)).exists();

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
            case "title" -> asc ? content.title.asc() : content.title.desc();
            default -> content.createdAt.desc();
        };
    }

}

