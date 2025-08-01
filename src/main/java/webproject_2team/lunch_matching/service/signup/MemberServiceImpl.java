package webproject_2team.lunch_matching.service.signup;

import webproject_2team.lunch_matching.domain.signup.Member;
import webproject_2team.lunch_matching.domain.signup.MemberRole; // MemberRole 임포트
import webproject_2team.lunch_matching.dto.signup.MemberResponseDTO;
import webproject_2team.lunch_matching.dto.signup.MemberSignupDTO;
import webproject_2team.lunch_matching.dto.signup.MemberUpdateDTO;
import webproject_2team.lunch_matching.dto.signup.ProfileDTO;
import webproject_2team.lunch_matching.repository.MemberRepository; // MemberRepository 임포트
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnailator; // Thumbnailator 임포트

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthService emailAuthService; // 이메일 인증 서비스

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
        } // 로그인아이디중복
        if (isEmailExists(memberSignupDTO.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        } // 이메일중복
        if (isPhoneNumberExists(memberSignupDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        } // 전화번호중복

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
        member.changeNickname(finalNickname); // Member 엔티티에 닉네임 필드에 값을 설정하는 메서드 추가 필요

        // 6. 기본 역할 부여 (예: USER)
        member.addRole(MemberRole.USER);

        // 7. 프로필 사진 처리
        if (profileDTO != null && profileDTO.getFile() != null && !profileDTO.getFile().isEmpty()) {
            try {
                processAndSaveProfileImage(member, profileDTO.getFile());
            } catch (IOException e) {
                log.error("File upload failed: {}", e.getMessage());
                throw new RuntimeException("프로필 사진 업로드에 실패했습니다.", e);
            }
        }

        // 8. 데이터베이스 저장
        try {
            Member savedMember = memberRepository.save(member);
            log.info("회원 가입 성공: {}", savedMember.getUsername());
            emailAuthService.removeAuthInfo(memberSignupDTO.getEmail()); // <-- DB에 저장한 후 인증삭제.
            log.info("회원가입 성공 및 이메일 인증 정보 삭제 완료: {}", memberSignupDTO.getEmail());
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
        String folderPath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File uploadPathFolder = new File(uploadPath, folderPath);
        if (!uploadPathFolder.exists()) {
            uploadPathFolder.mkdirs(); // 폴더가 없으면 생성
        }
        return folderPath;
    }

    /**
     * 프로필 이미지 업로드 및 Member 엔티티에 정보 저장 로직
     * @param member 프로필 이미지를 업데이트할 Member 엔티티
     * @param multipartFile 업로드된 MultipartFile
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    private void processAndSaveProfileImage(Member member, MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String savedFileName = uuid + "_" + originalFileName;

        String folderPath = makeFolder(); // 년/월/일 폴더 경로 (예: 2025/07/31)
        Path savePath = Paths.get(uploadPath, folderPath, savedFileName); // 실제 저장될 파일의 전체 경로

        Files.createDirectories(savePath.getParent()); // 상위 디렉토리 생성 (예: /upload/2025/07/31)
        multipartFile.transferTo(savePath); // 파일 저장

        boolean isImage = Files.probeContentType(savePath).startsWith("image");
        if (isImage) {
            // 썸네일 파일 경로: /upload/2025/07/31/s_uuid_filename.jpg
            File thumbnailFile = new File(uploadPath + File.separator + folderPath, "s_" + savedFileName);
            Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 200, 200); // 200x200 썸네일 생성
        }

        // Member 엔티티에 프로필 이미지 정보 업데이트 (savePath는 폴더 경로만 저장)
        member.updateProfileImage(uuid, originalFileName, folderPath);
    }

    /**
     * 프로필 이미지 파일 삭제 도우미 메서드
     * @param folderPath 이미지 파일이 저장된 폴더 경로 (예: 2025/07/31)
     * @param uuid 파일의 UUID
     * @param fileName 원본 파일명
     * @param includeThumbnail 썸네일도 함께 삭제할지 여부
     */
    private void deleteProfileFile(String folderPath, String uuid, String fileName, boolean includeThumbnail) {
        if (folderPath == null || uuid == null || fileName == null) return;

        // 원본 파일 경로: /upload/2025/07/31/uuid_filename.jpg
        String fullFilePath = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;
        File fullFile = new File(fullFilePath);
        if (fullFile.exists()) {
            fullFile.delete();
            log.info("원본 프로필 이미지 삭제: {}", fullFilePath);
        }

        if (includeThumbnail) {
            // 썸네일 파일 경로: /upload/2025/07/31/s_uuid_filename.jpg
            String thumbnailFilePath = uploadPath + File.separator + folderPath + File.separator + "s_" + uuid + "_" + fileName;
            File thumbnailFile = new File(thumbnailFilePath);
            if (thumbnailFile.exists()) {
                thumbnailFile.delete();
                log.info("썸네일 프로필 이미지 삭제: {}", thumbnailFilePath);
            }
        }
    }

    @Override
    public boolean isUsernameExists(String username) {
        return memberRepository.existsByUsername(username);
    }

    @Override
    public boolean isNameExists(String name) {
        return false; // 이름 중복 확인은 현재 구현되어 있지 않음
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

    // username(사용자 아이디)를 가지고 정보 조회하기. (반환 타입 MemberResponseDTO로 변경)
    @Override
    public MemberResponseDTO getMemberByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 회원을 찾을 수 없습니다. Username: " + username));

        return entityToResponseDTO(member);
    }



    // ID로 회원 정보 조회
    @Override
    public MemberResponseDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다. ID: " + id));

        return entityToResponseDTO(member);
    }

    // 회원 정보 수정
    @Override
    public void updateMember(String username, MemberUpdateDTO memberUpdateDTO, ProfileDTO profileDTO) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("수정할 회원을 찾을 수 없습니다. Username: " + username));

        // 닉네임 중복 확인 (본인 닉네임 제외)
        if (!member.getNickname().equals(memberUpdateDTO.getNickname()) && isNicknameExists(memberUpdateDTO.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        // 전화번호 중복 확인 (본인 전화번호 제외)
        if (!member.getPhoneNumber().equals(memberUpdateDTO.getPhoneNumber()) && isPhoneNumberExists(memberUpdateDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        // 닉네임 및 전화번호 업데이트
        member.changeNickname(memberUpdateDTO.getNickname());
        member.changePhoneNumber(memberUpdateDTO.getPhoneNumber());

        // --- 비밀번호 변경 로직 ---
        // 새로운 비밀번호가 입력된 경우에만 처리
        if (memberUpdateDTO.getNewPassword() != null && !memberUpdateDTO.getNewPassword().isEmpty()) {
            // 1. 현재 비밀번호 일치 여부 확인 (필요시)
            // if (memberUpdateDTO.getCurrentPassword() == null ||
            //     !passwordEncoder.matches(memberUpdateDTO.getCurrentPassword(), member.getPassword())) {
            //     throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            // }

            // 2. 새로운 비밀번호와 확인 비밀번호 일치 여부 확인
            if (!memberUpdateDTO.getNewPassword().equals(memberUpdateDTO.getConfirmNewPassword())) {
                throw new IllegalArgumentException("새로운 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }

            // 3. 새로운 비밀번호 암호화 및 업데이트
            member.changePassword(passwordEncoder.encode(memberUpdateDTO.getNewPassword()));
            log.info("회원 비밀번호 변경 성공, Member Username: {}", username);
        }
        // ----------------------------

        // 프로필 이미지 처리
        if (memberUpdateDTO.isDeleteProfileImage()) {
            // 기존 파일 삭제 로직 추가 (파일 시스템에서)
            if (member.isHasProfileImage()) {
                deleteProfileFile(member.getProfileImagePath(), member.getProfileImageUuid(), member.getProfileImageName(), true);
            }
            member.deleteProfileImage(); // 엔티티의 이미지 정보 초기화
        } else if (profileDTO != null && profileDTO.getFile() != null && !profileDTO.getFile().isEmpty()) {
            // 새로운 이미지가 업로드된 경우
            // 기존 파일 삭제 로직 추가 (파일 시스템에서)
            if (member.isHasProfileImage()) {
                deleteProfileFile(member.getProfileImagePath(), member.getProfileImageUuid(), member.getProfileImageName(), true);
            }
            // 새로운 파일 업로드 로직
            try {
                processAndSaveProfileImage(member, profileDTO.getFile());
            } catch (IOException e) {
                log.error("프로필 사진 업데이트 중 오류: {}", e.getMessage(), e);
                throw new RuntimeException("프로필 사진 업데이트에 실패했습니다.", e);
            }
        }
        // else: profileImage가 null이고 deleteProfileImage도 false면 이미지 변경 없음

        memberRepository.save(member);
        log.info("회원 정보 수정 성공, Member Username: {}", username);
    }

    // 회원 삭제
    @Override
    @Transactional // 삭제는 트랜잭션으로 묶는 것이 안전
    public void deleteMember(String username) {
        Optional<Member> result = memberRepository.findByUsername(username);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("삭제할 회원을 찾을 수 없습니다. Username: " + username);
        }
        Member member = result.get();

        // 프로필 이미지 파일 삭제 (있을 경우)
        if (member.isHasProfileImage()) {
            deleteProfileFile(member.getProfileImagePath(), member.getProfileImageUuid(), member.getProfileImageName(), true);
        }

        memberRepository.delete(member); // 엔티티를 직접 전달하여 삭제
        log.info("회원 삭제 성공, Member Username: {}", username);
    }

    /**
     * Member 엔티티를 MemberResponseDTO로 변환하는 헬퍼 메서드
     * @param member 변환할 Member 엔티티
     * @return 변환된 MemberResponseDTO
     */
    private MemberResponseDTO entityToResponseDTO(Member member) {
        MemberResponseDTO dto = modelMapper.map(member, MemberResponseDTO.class);

        // 프로필 이미지 URL 설정 (uploadPath는 필드로 주입받아야 함)
        // 실제 서비스에서는 파일 서버의 URL을 반환해야 합니다. 여기서는 로컬 경로 예시.
        // 클라이언트에서 접근 가능한 URL로 변환하는 로직 필요 (예: /uploads/2025/07/31/uuid_filename.jpg)
        if (member.isHasProfileImage() && member.getProfileImageUuid() != null && member.getProfileImageName() != null) {
            String baseUrl = "/uploads"; // WebConfig1.java에 설정된 정적 리소스 핸들러의 기본 URL
            String fullPath = baseUrl + "/" + member.getProfileImagePath() + "/" +
                    member.getProfileImageUuid() + "_" + member.getProfileImageName();
            String thumbnailPath = baseUrl + "/" + member.getProfileImagePath() + "/" +
                    "s_" + member.getProfileImageUuid() + "_" + member.getProfileImageName();

            dto.setProfileImageUrl(fullPath);
            dto.setProfileThumbnailUrl(thumbnailPath);
        }
        return dto;
    }
}
