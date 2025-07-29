package webproject_2team.lunch_matching.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailAuthServiceImpl implements EmailAuthService{
    private final JavaMailSender mailSender;

    private final Map<String, AuthInfo> authStorage = new ConcurrentHashMap<>();

    private static final long EXPIRE_MINUTES = 5;

    @Override
    public void sendAuthCode(String email) {
        String code = createCode();

        // 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[오점뭐] 이메일 인증번호 안내");
        message.setText("인증번호: " + code + "\n유효시간: 5분");
        mailSender.send(message);

        // 메모리에 저장
        authStorage.put(email, new AuthInfo(code, LocalDateTime.now()));
    }

    @Override
    public boolean verifyAuthCode(String email, String inputCode) {
        AuthInfo authInfo = authStorage.get(email);
        // 1. 인증 정보가 없는 경우 (잘못된 이메일 또는 인증 요청을 하지 않음)
        if (authInfo == null) return false;

        // 2. 시간 만료 확인
        if (Duration.between(authInfo.createdAt, LocalDateTime.now()).toMinutes() >= EXPIRE_MINUTES) {
            authStorage.remove(email);
            return false;
        }

        // 3. 인증 코드 일치 여부 확인
        boolean isMatched = authInfo.code.equals(inputCode);
        if (isMatched) {
            authStorage.remove(email); // 인증 성공 시 정보 삭제 (일회용)
        }
        return isMatched;
    }

    // 6자리 인증코드 생성
    private String createCode() {
        Random random = new Random();
        // 0부터 999999까지의 숫자 중 6자리
        return String.format("%06d", random.nextInt(1000000));
    }

    // 내부 저장용 클래스
    private static class AuthInfo {
        String code;
        LocalDateTime createdAt;

        public AuthInfo(String code, LocalDateTime createdAt) {
            this.code = code;
            this.createdAt = createdAt;
        }
    }
}