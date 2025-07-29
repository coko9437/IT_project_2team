package webproject_2team.lunch_matching.controller;

import webproject_2team.lunch_matching.dto.EmailAuthRequestDTO;
import webproject_2team.lunch_matching.dto.EmailAuthVerifyDTO;
import webproject_2team.lunch_matching.service.EmailAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailAuthController {
    private final EmailAuthService emailAuthService;

    // 인증번호 요청
    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@Valid @RequestBody EmailAuthRequestDTO authRequestDTO) {
        emailAuthService.sendAuthCode(authRequestDTO.getEmail());
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

    // 인증번호 검증
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody EmailAuthVerifyDTO authVerifyDTO) {
        boolean result = emailAuthService.verifyAuthCode(
                authVerifyDTO.getEmail(), authVerifyDTO.getAuthCode());
        return result ? ResponseEntity.ok("인증 성공") :
                ResponseEntity.badRequest().body("인증번호가 일치하지 않거나 만료되었습니다.");
    }
}