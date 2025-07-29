package webproject_2team.lunch_matching.service;

import webproject_2team.lunch_matching.dto.MemberSignupDTO;
import webproject_2team.lunch_matching.dto.ProfileDTO;

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

    // TODO: 로그인 메서드, 회원 정보 조회/수정/삭제 메서드 등을 추가예정임.
}

