package webproject_2team.lunch_matching.controller;

import  webproject_2team.lunch_matching.dto.KakaoLocalSearchResponse;
import  webproject_2team.lunch_matching.service.KakaoApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@Log4j2
@RequiredArgsConstructor
public class MapSearchController {

    private final KakaoApiService kakaoApiService;

    @GetMapping("/lunchmatch/searchKakaoPlaces")
    @ResponseBody
    public KakaoLocalSearchResponse searchKakaoPlaces(
            @RequestParam("query") String query,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        log.info("카카오 장소 검색 요청: query={}, page={}", query, page);

        KakaoLocalSearchResponse searchResult = kakaoApiService.searchPlacesByKeyword(query, page);

        if (searchResult == null) {
            log.warn("카카오 API로부터 검색 결과를 받지 못했습니다. query={}", query);
            return KakaoLocalSearchResponse.builder()
                    .documents(java.util.Collections.emptyList())
                    .meta(new KakaoLocalSearchResponse.Meta())
                    .build();
        }

        log.info("카카오 API 검색 결과 문서 수: {}", searchResult.getDocuments().size());
        return searchResult;
    }
}