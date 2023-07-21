package com.ll.TeamSteam.domain.matching.repository;

import com.ll.TeamSteam.domain.matching.entity.Matching;
import com.ll.TeamSteam.domain.matching.entity.QMatching;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
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
    public Page<Matching> filterByGenreAndStartTimeAndGender(GenreTagType genreType, Integer startTime, String gender, Pageable pageable) {
        QMatching matching = QMatching.matching;

        // 필터링 조건을 담을 Predicate 변수 선언
        BooleanBuilder predicate = new BooleanBuilder();

        // 각각의 파라미터가 null이 아닌 경우에만 해당 필터를 추가
        if (genreType != null) {
            predicate.and(matching.genre.eq(genreType));
        }

        if (startTime != null) {
            predicate.and(matching.startTime.eq(startTime));
        }

        if (gender != null && !gender.isEmpty()) {
            predicate.and(matching.gender.eq(gender));
        }

        // 정렬 조건 생성
        OrderSpecifier<?> orderSpecifier = sortMatching(pageable);

        // fetch()와 select(Expressions.ONE).fetchOne()를 따로 사용
        List<Matching> results = queryFactory.selectFrom(matching)
                .where(predicate)
                .orderBy(orderSpecifier) // 외부에서 정렬 조건을 넘겨받음
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
