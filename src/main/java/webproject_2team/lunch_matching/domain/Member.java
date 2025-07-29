package webproject_2team.lunch_matching.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "member_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 고유 ID (Primary Key)

    @Column(unique = true, nullable = false, length = 20) // ID는 고유하고 필수이며 길이 제한
    private String username;

    @Column(nullable = false, length = 255) // 비밀번호는 암호화되므로 길이 길게
    private String password;

    @Column(unique = true, nullable = false, length = 20) // 전화번호 고유하고 필수이며 길이 제한
    private String phoneNumber;

    @Column(nullable = false, length = 10) // 이름 필드 추가 (필수, 길이 제한)
    private String name;

//    @Setter
    @Column(nullable = false) // LocalDate는 컬럼 길이 제한이 필요 없음
    private LocalDate birthDate; // DTO와 동일하게 LocalDate 타입으로 변경하는 것을 권장

    @Column(unique = true, nullable = false, length = 100) // 이메일은 고유하고 필수이며 길이 제한
    private String email;

    //이메일인증... 인증되면 true 아니면 false(근데 인증못받으면 회원가입안됨)
    @Column(nullable = false)
    private boolean isEmailVerified; // 이메일 인증 완료 여부
    /// ////////////

    @Column(unique = true, nullable = false, length = 20) // 닉네임은 고유하고 필수이며 길이 제한
    private String nickname;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    private LocalDateTime modDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "role_name", nullable = false)
    private Set<MemberRole> roles = new HashSet<>();

    // --- 프로필 사진 관련 필드 추가 ---
    @Column(length = 255)
    private String profileImageUuid;
    @Column(length = 255)
    private String profileImageName;
    @Column(length = 500)
    private String profileImagePath;
    private boolean hasProfileImage;
    // ---------------------------------

    // 비밀번호 변경 메서드
    public void changePassword(String password) {
        this.password = password;
    }

    // 이메일 변경 메서드 (필요하다면)
    public void changeEmail(String email) {
        this.email = email;
    }

    // 역할 추가 메서드
    public void addRole(MemberRole role) {
        this.roles.add(role);
    }

    // 닉네임 설정 메서드 (Service에서 최종 닉네임을 설정할 때 사용)
    public void addNickname(String nickname) { // addNickname으로 변경
        this.nickname = nickname;
    }

    // 프로필 이미지 정보 업데이트 메서드 (편의를 위해)
    public void updateProfileImage(String uuid, String fileName, String savePath) {
        this.profileImageUuid = uuid;
        this.profileImageName = fileName;
        this.profileImagePath = savePath + "/" + uuid + "_" + fileName;
        this.hasProfileImage = true;
    }

    // 프로필 이미지 삭제 메서드 (추후 구현)
    public void deleteProfileImage() {
        this.profileImageUuid = null;
        this.profileImageName = null;
        this.profileImagePath = null;
        this.hasProfileImage = false;
    }
}