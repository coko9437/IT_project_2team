package webproject_2team.lunch_matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type; // 검색 타입 (title, content, writer)

    private String keyword; // 검색 키워드

    private String genderFilter; // 성별 필터

    private String foodFilter; // 음식 필터

    public Pageable getPageable(String...props) {
        return PageRequest.of(this.page -1, this.size, Sort.by(props).descending());
    }

    public String[] getTypes() {
        if (type == null || type.trim().length() == 0) {
            return null;
        }
        return type.split("");
    }

    public String getGenderFilter() {
        return genderFilter;
    }

    public String getFoodFilter() {
        return foodFilter;
    }
}