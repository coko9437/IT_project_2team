package webproject_2team.lunch_matching.mapper;

import  webproject_2team.lunch_matching.dto.LunchMatchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LunchMatchMapper {
    // 이 부분이 정확히 일치해야 합니다.
    void insert(LunchMatchDTO lunchMatchDTO); // <-- 이 메서드 선언을 확인하세요.

    LunchMatchDTO selectOne(Long rno);
    List<LunchMatchDTO> selectAll();
    void update(LunchMatchDTO lunchMatchDTO);
    void delete(Long rno);
    List<LunchMatchDTO> searchAndSort(String keyword, String category, Double minRating, String orderBy);
    List<LunchMatchDTO> selectListWithPaging(int limit, int offset);
    int getTotalCount();
}