package webproject_2team.lunch_matching.repository;

import  webproject_2team.lunch_matching.domain.LunchMatch;

import java.util.List;

public interface LunchMatchRepositoryCustom {

    // 동적 쿼리를 위한 메서드 정의
    List<LunchMatch> search(List<String> keywords, String category, Double minRating, String orderBy);
}