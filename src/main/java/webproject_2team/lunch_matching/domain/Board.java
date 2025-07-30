package webproject_2team.lunch_matching.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob // 내용이 길어질 수 있으므로 @Lob 어노테이션 유지
    private String content;

    private String writer;

    // 기존 region 필드는 넓은 범위의 지역 정보 (예: "부산진구")로 유지하고,
    // 지도에서 선택한 정확한 위치를 위해 위도, 경도, 장소명을 추가합니다.
    private String region;

    // --- 지도 관련 필드 추가 시작 ---
    // 위도 (latitude): 지도의 세로 좌표 (Double 타입)
    private Double latitude;
    // 경도 (longitude): 지도의 가로 좌표 (Double 타입)
    private Double longitude;
    // locationName: 지도에서 선택된 장소의 구체적인 이름 (예: "부산광역시 부산진구 부전동 롯데백화점 부산본점")
    private String locationName;
    // --- 지도 관련 필드 추가 끝 ---

    private String genderLimit; // 성별 제한 (남, 여, 성별상관무)

    private String foodCategory; // 음식 카테고리 (한식, 중식 등)

    private String imagePath; // 게시글에 첨부된 이미지 경로 (선택 사항)

    private LocalDateTime createdAt; // 게시글 생성 시간

    private Integer deadlineHours; // 마감시간 (시간 단위: 1, 2, 3, 24 등)

    private LocalDateTime deadlineAt; // 게시글의 실제 마감 시간 (createdAt + deadlineHours)

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments; // 이 게시글에 달린 댓글 목록

    /**
     * 게시글이 마감되었는지 확인하는 메서드
     * @return 마감 시간이 현재 시간보다 이전이면 true, 아니면 false
     */
    public boolean isExpired() {
        if (deadlineAt == null) { // 마감 시간이 설정되지 않았다면 마감되지 않은 것으로 간주
            return false;
        }
        return LocalDateTime.now().isAfter(deadlineAt);
    }

    /**
     * 게시글 마감까지 남은 시간을 분 단위로 계산하는 메서드
     * @return 남은 시간 (분), 마감되었거나 마감 시간이 없으면 0 반환
     */
    public long getRemainingMinutes() {
        if (deadlineAt == null || isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), deadlineAt).toMinutes();
    }

    /**
     * 게시글의 주요 정보를 수정하는 메서드
     * 이 메서드를 통해 엔티티의 상태를 변경합니다.
     * @param title 새로운 제목
     * @param content 새로운 본문 내용
     * @param region 새로운 지역 (광역)
     * @param genderLimit 새로운 성별 제한
     * @param foodCategory 새로운 음식 카테고리
     * @param deadlineHours 새로운 마감 시간 (시간 단위)
     * @param imagePath 새로운 이미지 경로 (기존 이미지 경로 유지 시 동일 값)
     * @param latitude 새로운 위도 (지도)
     * @param longitude 새로운 경도 (지도)
     * @param locationName 새로운 장소명 (지도)
     */
    public void change(String title, String content, String region, String genderLimit, String foodCategory,
                       Integer deadlineHours, String imagePath, Double latitude, Double longitude, String locationName) {
        this.title = title;
        this.content = content;
        this.region = region;
        this.genderLimit = genderLimit;
        this.foodCategory = foodCategory;
        this.deadlineHours = deadlineHours;
        this.imagePath = imagePath; // 이미지 경로는 수정 시에도 업데이트될 수 있음
        // --- 지도 관련 필드 업데이트 ---
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        // ------------------------------
        // createdAt은 변경되지 않으며, deadlineAt은 BoardService에서 다시 계산됩니다.
    }
}