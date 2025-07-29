package webproject_2team.lunch_matching.service;

public interface ReviewLikeService {
    // 좋아요 추가 또는 취소
    boolean toggleLike(Long reviewId, String memberId);

    // 특정 리뷰의 좋아요 수 조회
    int getLikeCount(Long reviewId);

    // 특정 사용자가 특정 리뷰에 좋아요를 눌렀는지 확인
    boolean isLiked(Long reviewId, String memberId);
}