package webproject_2team.lunch_matching.service.notice;

import webproject_2team.lunch_matching.domain.Notice;
import webproject_2team.lunch_matching.dto.notice.NoticeDTO;

import java.util.List;

public interface NoticeService {

    // 공지사항 등록 (관리자용)
    Long register(NoticeDTO noticeDTO);

    // 공지사항 상세 조회
    NoticeDTO read(Long id);

    // 공지사항 목록 조회
    List<NoticeDTO> getList();

    // 공지사항 수정
    void update(NoticeDTO noticeDTO);

    // 공지사항 삭제
    void delete(Long id);

    // DTO를 Entity로 변환
    default Notice dtoToEntity(NoticeDTO noticeDTO) {
        return Notice.builder()
                .id(noticeDTO.getId())
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .writer(noticeDTO.getWriter())
                .createdAt(noticeDTO.getCreatedAt())
                .viewCount(noticeDTO.getViewCount())
                .build();
    }

    // Entity를 DTO로 변환
    default NoticeDTO entityToDto(Notice notice) {
        return NoticeDTO.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .writer(notice.getWriter())
                .createdAt(notice.getCreatedAt())
                .viewCount(notice.getViewCount())
                .build();
    }
}