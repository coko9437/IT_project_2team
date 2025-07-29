package webproject_2team.lunch_matching.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import webproject_2team.lunch_matching.domain.Review;
import webproject_2team.lunch_matching.repository.search.ReviewSearch;

// JpaRepository, QuerydslPredicateExecutor, ReviewSearch 모두 상속
public interface ReviewRepository extends JpaRepository<Review, Long>, QuerydslPredicateExecutor<Review>, ReviewSearch {
    @Query("select r from Review r left join fetch r.fileList where r.review_id = :review_id")
    java.util.Optional<Review> findByIdWithFiles(@Param("review_id") Long review_id);
}