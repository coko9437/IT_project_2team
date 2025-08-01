package webproject_2team.lunch_matching.service.signup;

import webproject_2team.lunch_matching.domain.signup.Member;
import webproject_2team.lunch_matching.dto.signup.MemberResponseDTO;
import webproject_2team.lunch_matching.dto.signup.MemberSignupDTO;
import webproject_2team.lunch_matching.dto.signup.MemberUpdateDTO;
import webproject_2team.lunch_matching.dto.signup.ProfileDTO;

public interface MemberService {
    Long registerMember(MemberSignupDTO memberSignupDTO, ProfileDTO profileDTO);

    // 사용자 ID 중복 확인
    boolean isUsernameExists(String username);
    // 이름 중복 확인
    boolean isNameExists(String name);
    // 닉네임 중복 확인
    boolean isNicknameExists(String nickname);
    // 이메일 중복 확인
    boolean isEmailExists(String email);
    // 전화번호 중복 확인
    boolean isPhoneNumberExists(String phoneNumber);

    // username(사용자 아이디)를 가지고 정보 조회하기.
//    Member getMemberByUsername(String username);
    MemberResponseDTO getMemberByUsername(String username);

//    MemberResponseDTO getMemberByEmail(String email);

    // 회원 정보 조회 (ID로 조회하는 경우도 추가)
    MemberResponseDTO getMemberById(Long id);

    // 회원 정보 수정 (username 기준으로 수정)
    void updateMember(String username, MemberUpdateDTO memberUpdateDTO, ProfileDTO profileDTO);

    // 회원 삭제 (username 기준으로 삭제)
    void deleteMember(String username);


}

