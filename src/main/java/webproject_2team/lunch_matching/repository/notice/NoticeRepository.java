package webproject_2team.lunch_matching.repository.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import webproject_2team.lunch_matching.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}