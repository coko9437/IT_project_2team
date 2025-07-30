package webproject_2team.lunch_matching.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webproject_2team.lunch_matching.domain.Review;
import webproject_2team.lunch_matching.domain.ReviewLike;
import webproject_2team.lunch_matching.event.ReviewLikeUpdateEvent;
import webproject_2team.lunch_matching.repository.ReviewLikeRepository;
import webproject_2team.lunch_matching.repository.ReviewRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ReviewLikeServiceImpl implements ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public boolean toggleLike(Long reviewId, String memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + reviewId));

        Optional<ReviewLike> existingLike = reviewLikeRepository.findByReview_Review_idAndMemberId(review.getReview_id(), memberId);

        if (existingLike.isPresent()) {
            reviewLikeRepository.delete(existingLike.get());
            log.info("좋아요 취소 완료 - 리뷰 ID: {}, 회원 ID: {}", reviewId, memberId);
            updateReviewLikeCount(reviewId);
            return false;
        } else {
            ReviewLike reviewLike = ReviewLike.builder()
                    .review(review)
                    .member_id(memberId)
                    .build();
            
            reviewLikeRepository.save(reviewLike);
            log.info("좋아요 추가 완료 - 리뷰 ID: {}, 회원 ID: {}", reviewId, memberId);
            updateReviewLikeCount(reviewId);
            return true;
        }
    }

    @Override
    public int getLikeCount(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + reviewId));
        return reviewLikeRepository.countByReview(review);
    }

    @Override
    public boolean isLiked(Long reviewId, String memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + reviewId));
        return reviewLikeRepository.findByReview_Review_idAndMemberId(review.getReview_id(), memberId).isPresent();
    }

    private void updateReviewLikeCount(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + reviewId));
        int likeCount = reviewLikeRepository.countByReview(review);
        eventPublisher.publishEvent(new ReviewLikeUpdateEvent(this, reviewId, likeCount));
    }
}