package webproject_2team.lunch_matching.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity // 이 클래스가 JPA 엔티티임을 선언하며, 데이터베이스 테이블과 매핑됩니다.
@Table(name = "tbl_user") // 매핑될 테이블 이름을 "tbl_user"로 지정합니다.
@Getter // Lombok: 모든 필드에 대한 Getter 메서드를 자동 생성합니다.
@Builder // Lombok: 빌더 패턴을 사용하여 객체를 생성할 수 있게 합니다.
@AllArgsConstructor // Lombok: 모든 필드를 인자로 받는 생성자를 자동 생성합니다.
@NoArgsConstructor // Lombok: 기본 생성자(인자 없는 생성자)를 자동 생성합니다.
@ToString // Lombok: toString() 메서드를 자동 생성하여 객체 정보를 쉽게 출력할 수 있게 합니다.
public class User {

    @Id // 이 필드가 테이블의 기본 키(Primary Key)임을 선언합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성 전략을 IDENTITY로 설정합니다. (DB가 자동 증가)
    private Long id; // 사용자 번호 (Primary Key) - 'uno'에서 'id'로 변경

    @Column(length = 50, nullable = false, unique = true) // 컬럼 속성: 길이 50, null 허용 안함, 유니크
    private String userId; // 사용자 ID

    @Column(length = 255, nullable = false) // 컬럼 속성: 길이 255로 변경 (비밀번호 암호화 대비), null 허용 안함
    private String password; // 비밀번호

    @Column(length = 50, nullable = false)
    private String userName; // 사용자 이름

    @Column(length = 100)
    private String email; // 이메일

    @Column(length = 20) // 전화번호 (선택 사항, 필요 시 추가)
    private String phone;
}