package webproject_2team.lunch_matching.repository;

import  webproject_2team.lunch_matching.domain.LunchMatch;
import  webproject_2team.lunch_matching.domain.QLunchMatch;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LunchMatchRepositoryCustomImpl implements LunchMatchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LunchMatch> search(List<String> keywords, String category, Double minRating, String orderBy) {
        QLunchMatch lunchMatch = QLunchMatch.lunchMatch;
        BooleanBuilder builder = new BooleanBuilder();

        // 1. 검색어(keywords) 조건 추가 (리스트의 모든 키워드를 OR 조건으로 처리)
        if (keywords != null && !keywords.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            for (String keyword : keywords) {
                if (StringUtils.hasText(keyword)) { // 각 키워드가 유효할 때만 조건 추가
                    keywordBuilder.or(lunchMatch.name.containsIgnoreCase(keyword)
                            .or(lunchMatch.address.containsIgnoreCase(keyword)));
                }
            }
            if (keywordBuilder.getValue() != null) { // 실제 유효한 키워드 조건이 추가되었을 때만 전체 빌더에 추가
                builder.and(keywordBuilder);
            }
        }

        // 2. 카테고리(category) 조건 추가 (containsIgnoreCase로 유지)
        if (StringUtils.hasText(category)) {
            builder.and(lunchMatch.category.containsIgnoreCase(category));
        }

        // 3. 최소 평점(minRating) 조건 추가
        if (minRating != null) {
            builder.and(lunchMatch.rating.goe(minRating));
        }

        // 4. 정렬(orderBy) 조건 추가
        OrderSpecifier<?> orderSpecifier;
        if ("name".equalsIgnoreCase(orderBy)) {
            orderSpecifier = new OrderSpecifier<>(Order.ASC, lunchMatch.name);
        } else if ("rating_desc".equalsIgnoreCase(orderBy)) {
            orderSpecifier = new OrderSpecifier<>(Order.DESC, lunchMatch.rating).nullsLast();
        } else {
            // 기본 정렬: 최신순 (rno 내림차순)
            orderSpecifier = new OrderSpecifier<>(Order.DESC, lunchMatch.rno);
        }

        return queryFactory
                .selectFrom(lunchMatch)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();
    }
}