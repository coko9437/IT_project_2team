
package webproject_2team.lunch_matching.service;

import webproject_2team.lunch_matching.dto.ReviewCommentDTO;
import java.util.List;

public interface ReviewCommentService {
    // 댓글 등록
    Long register(ReviewCommentDTO reviewCommentDTO);

    // 특정 리뷰의 댓글 목록 조회
    List<ReviewCommentDTO> getCommentsOfReview(Long reviewId);

    // 댓글 수정
    void modify(ReviewCommentDTO reviewCommentDTO);

    // 댓글 삭제
    void remove(Long commentId);
}
