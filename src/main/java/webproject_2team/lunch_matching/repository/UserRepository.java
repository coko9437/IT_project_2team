package webproject_2team.lunch_matching.repository;

// 여기서 'lunch_match' -> 'Lunch_Match' (혹은 실제 프로젝트 패키지 이름)로 수정

import webproject_2team.lunch_matching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // 이 인터페이스가 Spring의 리포지토리 컴포넌트임을 나타냅니다.
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId); // 이 메서드는 나중에 필요 시 사용할 수 있습니다.
}