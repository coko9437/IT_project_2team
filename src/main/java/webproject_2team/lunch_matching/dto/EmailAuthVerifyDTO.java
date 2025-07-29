package webproject_2team.lunch_matching.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailAuthVerifyDTO {
    @NotBlank(message = "이메일은 필수 입력 값입니다.") // 유효성 검사 추가
    @Email(message = "유효한 이메일 주소를 입력해주세요.") // 유효성 검사 추가
    private String email;

    @NotBlank(message = "인증 코드는 필수 입력 값입니다.") // 유효성 검사 추가
    @Size(min = 6, max = 6, message = "인증 코드는 6자리 숫자입니다.") // 6자리 고정
    @Pattern(regexp = "^[0-9]+$", message = "인증 코드는 숫자만 가능합니다.") // 숫자만 허용
    private String authCode;
}