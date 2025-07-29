package webproject_2team.lunch_matching.service;


import webproject_2team.lunch_matching.dto.PageRequestDTO;
import webproject_2team.lunch_matching.dto.PageResponseDTO;
import webproject_2team.lunch_matching.dto.ReviewDTO;

public interface ReviewService {
    Long register(ReviewDTO reviewDTO);
    ReviewDTO readOne(Long review_id);
    void modify(ReviewDTO reviewDTO);
    void remove(Long review_id);
    PageResponseDTO<ReviewDTO> getList(PageRequestDTO pageRequestDTO);
    void updateLikeCount(Long reviewId, int likeCount);
}
