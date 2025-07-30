package webproject_2team.lunch_matching.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 맛집 정보를 담는 엔티티 클래스입니다.
 * 데이터베이스의 'lunch_match' 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "lunch_match") // 실제 테이블 이름 명시
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LunchMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno; // 맛집 등록 번호 (Primary Key)

    @Column(length = 200, nullable = false)
    private String name; // 맛집 이름

    @Column(length = 500, nullable = false)
    private String address; // 맛집 주소

    @Column(length = 50)
    private String phoneNumber; // 전화번호

    @Column(length = 100)
    private String category; // 음식 카테고리

    // --- 이 부분을 Double로 변경합니다 ---
    private Double latitude; // 위도 (Double 타입으로 변경)
    private Double longitude; // 경도 (Double 타입으로 변경)
    // ------------------------------------

    private Double rating; // 평점

    private String priceLevel; // 가격대

    @Column(length = 500)
    private String operatingHours; // 영업시간

    // 수정 기능을 위한 메서드 (JPA의 변경 감지 기능을 활용)
    // change 메서드의 파라미터도 Double로 변경해야 합니다.
    public void change(String name, String address, String phoneNumber, String category, Double latitude, Double longitude, Double rating, String priceLevel, String operatingHours) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.priceLevel = priceLevel;
        this.operatingHours = operatingHours;
    }
}