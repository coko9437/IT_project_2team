package webproject_2team.lunch_matching.service;

import webproject_2team.lunch_matching.domain.Member;
import webproject_2team.lunch_matching.dto.MemberSignupDTO;
import webproject_2team.lunch_matching.domain.MemberRole;
import webproject_2team.lunch_matching.dto.ProfileDTO;
import webproject_2team.lunch_matching.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements  MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthService emailAuthService;

    @Value("${com.busanit501.upload.path}") // application.properties에서 파일 업로드 경로 주입
    private String uploadPath;

    @Override
    public Long registerMember(MemberSignupDTO memberSignupDTO, ProfileDTO profileDTO) {
        // 1. DTO 유효성 검증 (Controller에서 @Valid로 처리되지만, 서비스에서도 핵심 로직 전 검증 권장)
        // 비밀번호와 비밀번호 확인 일치 여부
        if (!memberSignupDTO.getPassword().equals(memberSignupDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // 중복 확인
        if (isUsernameExists(memberSignupDTO.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (isEmailExists(memberSignupDTO.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (isPhoneNumberExists(memberSignupDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        // 닉네임 중복 처리 (랜덤 숫자 추가)
        String finalNickname = memberSignupDTO.getNickname();
        if (isNicknameExists(finalNickname)) {
            finalNickname = generateUniqueNickname(finalNickname);
        }

        //  2. 이메일 인증 코드 검증 로직 추가 **
        // MemberSignupDTO에 포함된 email과 emailAuthCode를 이용하여 검증
        boolean isEmailVerified = emailAuthService.verifyAuthCode(
                memberSignupDTO.getEmail(), memberSignupDTO.getEmailAuthCode());

        if (!isEmailVerified) {
            log.warn("이메일 인증에 실패했습니다: 이메일={}, 인증코드={}", memberSignupDTO.getEmail(), memberSignupDTO.getEmailAuthCode());
            throw new IllegalArgumentException("이메일 인증에 실패했습니다. 인증번호가 일치하지 않거나 만료되었습니다.");
        }
        // 이메일 인증 성공 시 메모리에 저장된 코드 삭제는 EmailAuthServiceImpl에서 이미 처리


        // 3. MemberSignupDTO -> Member Entity 변환
        Member member = modelMapper.map(memberSignupDTO, Member.class);

        // birthDate 필드가 null인지 확인하고 명시적으로 설정 (디버깅 또는 예외 처리 강화)
        if (memberSignupDTO.getBirthDate() == null) {
            log.error("MemberSignupDTO의 birthDate가 null입니다. DTO 파싱 또는 JSON 문제일 수 있습니다.");
            throw new IllegalArgumentException("생년월일 정보가 누락되었습니다."); // 예외 발생
        }
        member.setBirthDate(memberSignupDTO.getBirthDate()); // <-- 이 라인을 추가

        // 4. 비밀번호 암호화
        member.changePassword(passwordEncoder.encode(memberSignupDTO.getPassword()));

        // 5. 최종 닉네임 설정
        member.addNickname(finalNickname); // Member 엔티티에 닉네임 필드에 값을 설정하는 메서드 추가 필요

        // 6. 기본 역할 부여 (예: USER)
        member.addRole(MemberRole.USER);

        // 7. 프로필 사진 처리
        if (profileDTO != null && profileDTO.getFile() != null && !profileDTO.getFile().isEmpty()) {
            MultipartFile multipartFile = profileDTO.getFile();
            String originalFileName = multipartFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedFileName = uuid + "_" + originalFileName;

            // 저장 경로 생성 (년/월/일 폴더 구조)
            String folderPath = makeFolder();
            Path savePath = Paths.get(uploadPath + File.separator + folderPath, savedFileName);

            try {
                // 파일 저장
                multipartFile.transferTo(savePath);

                // 이미지 여부 확인 및 썸네일 생성
                boolean isImage = Files.probeContentType(savePath).startsWith("image");
                if (isImage) {
                    File thumbnailFile = new File(uploadPath + File.separator + folderPath, "s_" + savedFileName);
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 200, 200); // 200x200 썸네일
                }

                // Member 엔티티에 프로필 이미지 정보 업데이트
                member.updateProfileImage(uuid, originalFileName, folderPath); // folderPath는 "2025/07/25" 형태
                profileDTO.setUuid(uuid);
                profileDTO.setFileName(originalFileName);
                profileDTO.setSavePath(folderPath); // folderPath는 "2025/07/25" 형태
                profileDTO.setContentType(Files.probeContentType(savePath));
                profileDTO.setFileSize(multipartFile.getSize());
                profileDTO.setImg(isImage);

            } catch (IOException e) {
                log.error("File upload failed: {}", e.getMessage());
                throw new RuntimeException("프로필 사진 업로드에 실패했습니다.", e);
            }
        }

        // 8. 데이터베이스 저장
        try {
            Member savedMember = memberRepository.save(member);
            log.info("회원 가입 성공: {}", savedMember.getUsername());
            return savedMember.getId(); // 저장된 회원의 ID 반환
        } catch (DataIntegrityViolationException e) {
            log.error("회원 가입 중 데이터 무결성 위반 오류: {}", e.getMessage());
            // 여기서는 이미 isUsernameExists 등으로 중복을 체크했으므로,
            // 발생할 가능성이 낮지만, DB 제약 조건에 의한 최종 실패를 처리합니다.
            throw new RuntimeException("회원 가입에 실패했습니다. (데이터 중복 또는 형식 오류)", e);
        } catch (Exception e) {
            log.error("회원 가입 중 예상치 못한 오류: {}", e.getMessage());
            throw new RuntimeException("회원 가입 중 오류가 발생했습니다.", e);
        }
    }

    // 닉네임 중복 시 고유한 닉네임 생성 (예: "닉네임#0000")
    private String generateUniqueNickname(String baseNickname) {
        String newNickname = baseNickname;
        int attempt = 0;
        // 9999번까지 시도
        while (isNicknameExists(newNickname) && attempt < 10000) {
            String randomNumber = String.format("%04d", (int) (Math.random() * 10000));
            newNickname = baseNickname + "#" + randomNumber;
            attempt++;
        }
        if (attempt >= 10000) {
            throw new RuntimeException("닉네임을 생성할 수 없습니다. 다시 시도해주세요.");
        }
        return newNickname;
    }

    // 파일 저장 폴더 생성 (년/월/일)
    private String makeFolder() {
        String folderPath = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File uploadPathFolder = new File(uploadPath, folderPath);
        if (!uploadPathFolder.exists()) {
            uploadPathFolder.mkdirs(); // 폴더가 없으면 생성
        }
        return folderPath;
    }

    @Override
    public boolean isUsernameExists(String username) {
        return memberRepository.existsByUsername(username);
    }

    @Override
    public boolean isNameExists(String name) {
        return false;
    }

    @Override
    public boolean isNicknameExists(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public boolean isEmailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneNumberExists(String phoneNumber) {
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }
}
