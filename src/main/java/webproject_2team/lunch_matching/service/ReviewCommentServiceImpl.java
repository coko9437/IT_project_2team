package webproject_2team.lunch_matching.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webproject_2team.lunch_matching.domain.Review;
import webproject_2team.lunch_matching.domain.ReviewComment;
import webproject_2team.lunch_matching.dto.ReviewCommentDTO;
import webproject_2team.lunch_matching.repository.ReviewCommentRepository;
import webproject_2team.lunch_matching.repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ReviewCommentServiceImpl implements ReviewCommentService {

    private final ModelMapper modelMapper;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    @Override
    public Long register(ReviewCommentDTO reviewCommentDTO) {
        log.info("댓글 등록: {}", reviewCommentDTO);
        Review review = reviewRepository.findById(reviewCommentDTO.getReview_id())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + reviewCommentDTO.getReview_id()));

        ReviewComment reviewComment = modelMapper.map(reviewCommentDTO, ReviewComment.class);
        reviewComment.setReview(review);

        return reviewCommentRepository.save(reviewComment).getId();
    }

    @Override
    public List<ReviewCommentDTO> getCommentsOfReview(Long reviewId) {
        log.info("댓글 목록 조회: reviewId={}", reviewId);
        List<ReviewComment> comments = reviewCommentRepository.findByReview_Review_idOrderByRegDateAsc(reviewId);

        log.info("Fetched comments count: {}", comments.size());
        comments.forEach(comment -> log.info("  Comment: id={}, memberId={}, content={}", comment.getId(), comment.getMember_id(), comment.getContent()));

        return comments.stream()
                .map(reviewComment -> modelMapper.map(reviewComment, ReviewCommentDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void modify(ReviewCommentDTO reviewCommentDTO) {
        log.info("댓글 수정: {}", reviewCommentDTO);
        ReviewComment reviewComment = reviewCommentRepository.findById(reviewCommentDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + reviewCommentDTO.getId()));

        reviewComment.changeContent(reviewCommentDTO.getContent());
    }

    @Transactional
    @Override
    public void remove(Long commentId) {
        log.info("댓글 삭제: commentId={}", commentId);
        reviewCommentRepository.deleteById(commentId);
    }
}