package webproject_2team.lunch_matching.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import webproject_2team.lunch_matching.domain.Board;
import webproject_2team.lunch_matching.dto.PageRequestDTO;
import webproject_2team.lunch_matching.dto.PageResponseDTO;
import webproject_2team.lunch_matching.repository.BoardRepository;

import java.time.LocalDateTime; // LocalDateTime 임포트 추가
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public PageResponseDTO<Board> getBoardList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("id");

        Page<Board> result;
        String keyword = pageRequestDTO.getKeyword();
        String[] types = pageRequestDTO.getTypes();
        String genderFilter = pageRequestDTO.getGenderFilter();
        String foodFilter = pageRequestDTO.getFoodFilter();

        // 검색 조건이 있는 경우
        if (keyword != null && !keyword.trim().isEmpty() && types != null) {
            boolean searchTitle = false;
            boolean searchContent = false;
            boolean searchWriter = false;

            for (String type : types) {
                switch (type) {
                    case "t": // title
                        searchTitle = true;
                        break;
                    case "c": // content
                        searchContent = true;
                        break;
                    case "w": // writer
                        searchWriter = true;
                        break;
                }
            }

            result = boardRepository.findByKeywordAndTypeAndFilters(
                    keyword, searchTitle, searchContent, searchWriter,
                    genderFilter, foodFilter, pageable);
        }
        // 필터만 있는 경우
        else if ((genderFilter != null && !genderFilter.trim().isEmpty()) ||
                (foodFilter != null && !foodFilter.trim().isEmpty())) {
            result = boardRepository.findByFilters(genderFilter, foodFilter, pageable);
        }
        // 조건이 없는 경우
        else {
            result = boardRepository.findAll(pageable);
        }

        List<Board> dtoList = result.getContent().stream().collect(Collectors.toList());
        int totalCount = (int)result.getTotalElements();

        return PageResponseDTO.<Board>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .totalCount(totalCount)
                .build();
    }

    public Board read(Long id) {
        Optional<Board> result = boardRepository.findById(id);
        return result.orElse(null);
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    // 게시글 수정 메서드
    public void modify(Board board) {
        Optional<Board> existingBoardOpt = boardRepository.findById(board.getId());
        if (existingBoardOpt.isEmpty()) {
            throw new IllegalArgumentException("수정할 게시글을 찾을 수 없습니다.");
        }
        Board existingBoard = existingBoardOpt.get();

        // 기존 게시글의 생성 시간 유지 (수정 시에는 변경되지 않음)
        LocalDateTime originalCreatedAt = existingBoard.getCreatedAt();

        // Board 엔티티의 change 메서드에 모든 필드를 전달
        existingBoard.change(
                board.getTitle(),
                board.getContent(),
                board.getRegion(),
                board.getGenderLimit(),
                board.getFoodCategory(),
                board.getDeadlineHours(),
                board.getImagePath(), // 기존 imagePath 유지 또는 새 이미지 경로로 업데이트
                board.getLatitude(),   // 지도 관련 필드 추가
                board.getLongitude(),  // 지도 관련 필드 추가
                board.getLocationName()// 지도 관련 필드 추가
        );

        // 마감시간 재계산 (생성 시간 기준)
        if (board.getDeadlineHours() != null && originalCreatedAt != null) {
            existingBoard.setDeadlineAt(originalCreatedAt.plusHours(board.getDeadlineHours()));
        } else if (board.getDeadlineHours() != null) { // 예외 처리: originalCreatedAt이 없는 경우 (거의 없겠지만 방어적 코드)
            existingBoard.setDeadlineAt(LocalDateTime.now().plusHours(board.getDeadlineHours()));
        }

        boardRepository.save(existingBoard);
    }

    // 게시글 저장 메서드 (중복된 save 메서드 제거 후 통합)
    public Board save(Board board) {
        // createdAt이 컨트롤러에서 설정되지 않았다면 여기서 현재 시간으로 설정
        if (board.getCreatedAt() == null) {
            board.setCreatedAt(LocalDateTime.now());
        }

        // 마감시간 설정
        if (board.getDeadlineHours() != null && board.getCreatedAt() != null) {
            board.setDeadlineAt(board.getCreatedAt().plusHours(board.getDeadlineHours()));
        }

        // board 엔티티에 latitude, longitude, locationName 필드가 폼에서 자동으로 매핑되어 들어올 것입니다.
        return boardRepository.save(board);
    }

    // 성별 접근 권한 체크
    public boolean canAccessBoard(Board board, String gender) {
        if (board.getGenderLimit() == null || board.getGenderLimit().equals("성별상관무")) {
            return true;
        }
        return board.getGenderLimit().equals(gender);
    }
}