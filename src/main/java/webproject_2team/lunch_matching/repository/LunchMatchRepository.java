package webproject_2team.lunch_matching.repository;

import  webproject_2team.lunch_matching.domain.LunchMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * LunchMatch 엔티티에 대한 데이터베이스 작업을 처리하는 리포지토리입니다.
 */
@Repository
public interface LunchMatchRepository extends JpaRepository<LunchMatch, Long>, LunchMatchRepositoryCustom {

    /**
     * 맛집의 이름(name)과 주소(address)를 기준으로 데이터가 이미 존재하는지 확인합니다.
     * 중복 저장을 방지하기 위해 사용됩니다.
     * @param name 맛집 이름
     * @param address 맛집 주소
     * @return 존재하면 LunchMatch 객체를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    Optional<LunchMatch> findByNameAndAddress(String name, String address);

}