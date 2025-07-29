package webproject_2team.lunch_matching.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webproject_2team.lunch_matching.domain.Review;
import webproject_2team.lunch_matching.domain.ReviewLike;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    int countByReview(Review review);

    // 특정 리뷰에 특정 사용자가 좋아요를 눌렀는지 확인
    @Query("SELECT rl FROM ReviewLike rl WHERE rl.review.review_id = :reviewId AND rl.member_id = :memberId")
    Optional<ReviewLike> findByReview_Review_idAndMemberId(@Param("reviewId") Long reviewId, @Param("memberId") String memberId);

    // 특정 리뷰의 좋아요 수 계산
    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.review_id = :reviewId")
    int countByReview_Review_id(@Param("reviewId") Long reviewId);
}