package webproject_2team.lunch_matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewLikeDTO {

    private Long id;
    private Long review_id;
    private String member_id;
    private LocalDateTime regDate;

}