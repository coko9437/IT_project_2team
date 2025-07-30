package webproject_2team.lunch_matching.repository.search;


import org.springframework.data.domain.Page;
import webproject_2team.lunch_matching.domain.Review;
import webproject_2team.lunch_matching.dto.ReviewPageRequestDTO;

public interface ReviewSearch {
    Page<Review> searchAll(ReviewPageRequestDTO reviewPageRequestDTO, org.springframework.data.domain.Pageable pageable);
}
