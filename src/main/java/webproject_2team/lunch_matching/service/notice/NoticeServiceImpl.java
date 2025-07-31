package webproject_2team.lunch_matching.service.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webproject_2team.lunch_matching.domain.Notice;
import webproject_2team.lunch_matching.dto.notice.NoticeDTO;
import webproject_2team.lunch_matching.repository.notice.NoticeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public Long register(NoticeDTO noticeDTO) {
        Notice notice = dtoToEntity(noticeDTO);
        noticeRepository.save(notice);
        return notice.getId();
    }

    @Override
    public NoticeDTO read(Long id) {
        // ID로 공지사항을 찾습니다.
        Optional<Notice> result = noticeRepository.findById(id);

        // 만약 결과가 존재한다면,
        if (result.isPresent()) {
            Notice notice = result.get();

            // 조회수를 1 증가시키고 저장합니다.
            notice.setViewCount(notice.getViewCount() + 1);
            noticeRepository.save(notice);

            // DTO로 변환하여 반환합니다.
            return entityToDto(notice);
        } else {
            // 결과가 존재하지 않는다면, null을 반환합니다.
            return null;
        }
    }

    @Override
    public List<NoticeDTO> getList() {
        List<Notice> notices = noticeRepository.findAll();
        return notices.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // 👇 수정(update)과 삭제(delete) 메소드가 추가된 부분입니다.
    @Override
    public void update(NoticeDTO noticeDTO) {
        Optional<Notice> result = noticeRepository.findById(noticeDTO.getId());
        Notice notice = result.orElseThrow();

        // 제목과 내용만 수정합니다.
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());

        noticeRepository.save(notice);
    }

    @Override
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }
}