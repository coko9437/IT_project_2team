package webproject_2team.lunch_matching.service;

import  webproject_2team.lunch_matching.dto.LunchMatchDTO;

import java.util.List;

public interface LunchMatchService {
    // 맛집 정보 등록
    Long register(LunchMatchDTO lunchMatchDTO);

    // 특정 맛집 정보 조회
    LunchMatchDTO getOne(Long rno);

    // 모든 맛집 정보 조회
    List<LunchMatchDTO> getAll();

    // 맛집 정보 수정
    void modify(LunchMatchDTO lunchMatchDTO);

    // 맛집 정보 삭제
    void remove(Long rno);

    // 검색 및 정렬 (minRating 파라미터 유지)
    List<LunchMatchDTO> searchAndSort(String keyword, String category, Double minRating, String orderBy);

    // 페이징 처리된 목록 조회 (선택적)
    List<LunchMatchDTO> getListWithPaging(int limit, int offset);

    // 전체 맛집 개수 조회 (선택적)
    int getTotalCount();
}