package webproject_2team.lunch_matching.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webproject_2team.lunch_matching.domain.ReviewComment;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    // 특정 리뷰의 모든 댓글 조회
    @Query("SELECT rc FROM ReviewComment rc WHERE rc.review.review_id = :reviewId ORDER BY rc.regDate ASC")
    List<ReviewComment> findByReview_Review_idOrderByRegDateAsc(@Param("reviewId") Long reviewId);

}