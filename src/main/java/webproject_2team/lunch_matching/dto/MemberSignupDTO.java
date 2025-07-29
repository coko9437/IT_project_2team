package webproject_2team.lunch_matching.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupDTO {

    // Member 엔티티의 username에 매핑
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 영소문자 또는 숫자만 가능합니다.")
    private String username;

    // 비밀번호
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    // 정규식 추가: 최소 8자, 영문 대소문자, 숫자, 특수문자 중 3가지 이상 포함 (선택 사항, 보안 강화)
    // @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.")
    private String password;

    // 비밀번호 확인 (백엔드에서도 일치 여부 검증 로직 추가 필요)
    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String confirmPassword;

    @NotBlank(message = "휴대전화번호는 필수 입력 값입니다.")
    // 전화번호 정규식 추가 (선택 사항, 입력 형식에 따라 조정)
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    @Size(min = 10, max = 13, message = "전화번호는 10자 이상 13자 이하로 입력해주세요. (하이픈 포함)") // 하이픈 포함 시 13자
    private String phoneNumber; // 엔티티에 추가된 필드와 일치

    // 이름
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해주세요.")
    private String name;

    // 생년월일 (YYYYMMDD 형태)
    @NotNull(message = "생년월일은 필수 입력 값입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate; // DTO에서 LocalDate로 받으므로 엔티티도 LocalDate로 통일 권장

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @Size(max = 100, message = "이메일 주소는 100자 이하로 입력해주세요.") // 이메일 최대 길이 20자는 너무 짧을 수 있습니다.
    private String email;

    @NotBlank(message = "이메일 인증 코드는 필수 입력 값입니다.")
    private String emailAuthCode; // 사용자가 입력한 인증 코드를 받을 필드 추가 (서비스에서만 씀)

    // 닉네임 (중복 방지를 위한 랜덤 숫자 생성은 서비스/엔티티에서 처리)
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하로 입력해주세요.")
    private String nickname;
}