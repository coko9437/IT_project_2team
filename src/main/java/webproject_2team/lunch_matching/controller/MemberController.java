package webproject_2team.lunch_matching.controller;

import webproject_2team.lunch_matching.dto.MemberSignupDTO;
import webproject_2team.lunch_matching.dto.ProfileDTO;
import webproject_2team.lunch_matching.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/members") //기본 URL 경로를 /api/members
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
@Log4j2
public class MemberController {
    private final MemberService memberService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> signupMember(
            // ***** 이 부분이 가장 중요합니다: @RequestPart 사용 *****
            @Valid @RequestPart("memberSignupDTO") MemberSignupDTO memberSignupDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        log.info("회원가입 요청 수신: {}", memberSignupDTO.getUsername());

        // 비밀번호 확인 로직 추가 (DTO에 있으므로 Controller에서 먼저 검사하는 것이 좋음)
        if (!memberSignupDTO.getPassword().equals(memberSignupDTO.getConfirmPassword())) {
            log.warn("회원가입 실패: 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(Map.of("error", "비밀번호와 비밀번호 확인이 일치하지 않습니다."));
        }

        // ProfileDTO 생성 및 MultipartFile 설정
        ProfileDTO profileDTO = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileDTO = ProfileDTO.builder()
                    .file(profileImage) // MultipartFile을 ProfileDTO 내부에 설정
                    .build();
        }

        try {
            Long memberId = memberService.registerMember(memberSignupDTO, profileDTO);
            log.info("회원가입 성공, Member ID: {}", memberId);

            // 성공 응답 반환 (현재 코드 유지)
            return ResponseEntity.ok(Map.of("message", "회원가입이 성공적으로 완료되었습니다.", "memberId", memberId.toString()));

        } catch (IllegalArgumentException e) {
            // 사용자 입력 오류 (예: 중복 아이디, 비밀번호 불일치 등)
            // 비밀번호 불일치 예외는 위에서 처리했으니, 여기서는 주로 중복 관련 예외가 잡힐 것임.
            log.warn("회원가입 실패 (유효성 검사 또는 중복): {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // 그 외 서비스 계층에서 발생한 예외 (파일 업로드 실패 등)
            log.error("회원가입 중 서버 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "회원가입 중 서버 오류가 발생했습니다."));
        }
    }
}