
package webproject_2team.lunch_matching.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LunchMatchDTO {

    private Long rno;

    @NotEmpty
    private String name;

    @NotEmpty
    private String address;

    private String phoneNumber;
    private String category;

    // 위도와 경도는 숫자로 받는 것이 맞습니다.
    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Double rating;

    private String priceLevel;

    private String operatingHours;
}