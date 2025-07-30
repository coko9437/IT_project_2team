package webproject_2team.lunch_matching.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder // @Builder 대신 @SuperBuilder 사용
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDTO {

    private String type;
    private String keyword;

}