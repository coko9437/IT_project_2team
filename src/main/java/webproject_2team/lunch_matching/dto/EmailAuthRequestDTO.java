package webproject_2team.lunch_matching.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailAuthRequestDTO {
    @NotBlank(message = "이메일은 필수 입력 값입니다.") // 유효성 검사 추가
    @Email(message = "유효한 이메일 주소를 입력해주세요.") // 유효성 검사 추가
    private String email;
}