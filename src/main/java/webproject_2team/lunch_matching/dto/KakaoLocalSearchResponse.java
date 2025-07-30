package webproject_2team.lunch_matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoLocalSearchResponse {
    private Meta meta;
    private List<Document> documents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        @JsonProperty("total_count")
        private Integer totalCount;
        @JsonProperty("pageable_count")
        private Integer pageableCount;
        @JsonProperty("is_end")
        private Boolean isEnd;
        @JsonProperty("same_name")
        private SameName sameName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SameName {
        private List<String> region; // <---- 이 부분을 List<String>으로 변경했습니다.
        @JsonProperty("keyword")
        private String keywordAlias;
        @JsonProperty("selected_region")
        private String selectedRegion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        @JsonProperty("place_name")
        private String placeName;       // 장소명
        @JsonProperty("address_name")
        private String addressName;     // 전체 지번 주소 또는 도로명 주소
        private String x;               // 경도 (longitude)
        private String y;               // 위도 (latitude)
        private String category_name;   // 카테고리 그룹명 (예: 음식점 > 한식)
        private String phone;           // 전화번호
        private String place_url;       // 장소 상세페이지 URL
        @JsonProperty("road_address_name") // 도로명 주소
        private String roadAddressName;
        private String id;              // 장소 ID
        private String category_group_code; // 카테고리 그룹 코드
        private String category_group_name; // 카테고리 그룹 이름
    }
}