package webproject_2team.lunch_matching.repository;

import webproject_2team.lunch_matching.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByName(String name);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByPhoneNumber(String phoneNumber); // phoneNumber 필드 추가에 따라
    boolean existsByUsername(String username);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname); // 닉네임 중복 확인 메서드 추가
    boolean existsByPhoneNumber(String phoneNumber); // 전화번호 중복 확인 메서드 추가

}
