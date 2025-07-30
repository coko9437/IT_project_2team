package webproject_2team.lunch_matching.repository.search;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import webproject_2team.lunch_matching.domain.QReview;
import webproject_2team.lunch_matching.domain.Review;
import webproject_2team.lunch_matching.dto.ReviewPageRequestDTO;

import java.util.List;

@Log4j2
public class ReviewSearchImpl extends QuerydslRepositorySupport implements ReviewSearch {

    public ReviewSearchImpl() {
        super(Review.class);
    }

    @Override
    public Page<Review> searchAll(ReviewPageRequestDTO reviewPageRequestDTO, Pageable pageable) {
        QReview review = QReview.review;
        JPQLQuery<Review> query = from(review);

        // Fetch Join은 실제 데이터를 가져올 때만 사용하고, count 쿼리에는 영향을 주지 않도록 합니다.
        query.leftJoin(review.fileList).fetchJoin();

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // 검색 조건 빌더

        // 검색 조건 추가
        if (reviewPageRequestDTO.getType() != null && !reviewPageRequestDTO.getType().isEmpty() && reviewPageRequestDTO.getKeyword() != null) {
            String type = reviewPageRequestDTO.getType();
            switch (type) {
                case "c": // content
                    booleanBuilder.or(review.content.contains(reviewPageRequestDTO.getKeyword()));
                    break;
                case "m": // menu
                    booleanBuilder.or(review.menu.contains(reviewPageRequestDTO.getKeyword()));
                    break;
                case "p": // place
                    booleanBuilder.or(review.place.contains(reviewPageRequestDTO.getKeyword()));
                    break;
                case "w": // member_id (writer)
                    booleanBuilder.or(review.member_id.contains(reviewPageRequestDTO.getKeyword()));
                    break;
            }
        }

        // review_id > 0 조건 추가 (기본 조건)
        booleanBuilder.and(review.review_id.gt(0L));

        query.where(booleanBuilder); // 모든 조건을 query에 적용

        // 중복된 Review 엔티티 제거 (fetchJoin으로 인한 중복 방지)
        query.distinct();

        // 정렬 적용
        pageable.getSort().forEach(sort -> {
            com.querydsl.core.types.Order direction = sort.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
            String prop = sort.getProperty();
            if (prop.equals("review_id")) {
                query.orderBy(new com.querydsl.core.types.OrderSpecifier<>(direction, review.review_id));
            }
            // 다른 정렬 기준이 있다면 여기에 추가
        });

        // Querydsl 페이징 적용
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<Review> list = query.fetch(); // 페이징된 결과

        // 정확한 전체 개수를 가져오기 위한 별도의 쿼리
        // fileList와의 join 없이 Review 엔티티만 대상으로 distinct count
        JPQLQuery<Long> countQuery = from(review)
                .where(booleanBuilder) // 동일한 검색 조건 적용
                .select(review.countDistinct()); // distinct count

        long totalCount = countQuery.fetchOne(); // 전체 개수

        return new PageImpl<>(list, pageable, totalCount);
    }
}