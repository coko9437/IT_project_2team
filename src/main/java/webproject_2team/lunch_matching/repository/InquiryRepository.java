package webproject_2team.lunch_matching.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webproject_2team.lunch_matching.domain.Inquiry;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 작성자(writer)를 기준으로 문의 목록을 찾는 메서드 (내 문의내역 조회 시 사용)
    List<Inquiry> findByWriterOrderByIdDesc(String writer);

}