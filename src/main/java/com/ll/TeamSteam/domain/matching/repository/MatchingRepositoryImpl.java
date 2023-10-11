package com.ll.TeamSteam.domain.matching.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.entity.QMatching;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RequiredArgsConstructor
public class MatchingRepositoryImpl implements MatchingRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Matching> filterAndSearchByConditions(String name, String keyword, GenreTagType genreType, Integer startTime, String gender, Pageable pageable) {
        QMatching matching = QMatching.matching;

        BooleanBuilder predicate = new BooleanBuilder();

        if (genreType != null) {
            predicate.and(matching.genre.eq(genreType));
        }
        if (startTime != null) {
            predicate.and(matching.startTime.eq(startTime));
        }
        if (gender != null && !gender.isEmpty()) {
            predicate.and(matching.gender.eq(gender));
        }

        // 검색 조건 추가
        if (name != null && keyword != null) {
            switch (name) {
                case "title" -> predicate.and(matching.title.containsIgnoreCase(keyword));
                case "content" -> predicate.and(matching.content.containsIgnoreCase(keyword));
                case "writer" -> predicate.and(matching.user.username.containsIgnoreCase(keyword));
            }
        }

        OrderSpecifier<?> orderSpecifier = sortMatching(pageable);
        List<Matching> results = queryFactory.selectFrom(matching)
            .where(predicate)
            .orderBy(orderSpecifier)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory.select(Expressions.ONE.count()).from(matching)
            .where(predicate)
            .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private OrderSpecifier<?> sortMatching(Pageable page) {
        QMatching matching = QMatching.matching;

        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!page.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : page.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()){
                    case "createDate":
                        return new OrderSpecifier(direction, matching.createDate);
                }
            }
        }
        return null;
    }
}
