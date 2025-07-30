package webproject_2team.lunch_matching.service;

import webproject_2team.lunch_matching.domain.LunchMatch;
import webproject_2team.lunch_matching.dto.KakaoLocalSearchResponse;
import webproject_2team.lunch_matching.dto.LunchMatchDTO;
import webproject_2team.lunch_matching.repository.LunchMatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class LunchMatchServiceImpl implements LunchMatchService {

    private final ModelMapper modelMapper; // 주입은 받지만, 아래 메서드에서 사용하지 않도록 강제
    private final LunchMatchRepository lunchMatchRepository;
    private final KakaoApiService kakaoApiService;

    @Override
    public Long register(LunchMatchDTO lunchMatchDTO) {
        Optional<LunchMatch> existingMatch = lunchMatchRepository.findByNameAndAddress(
                lunchMatchDTO.getName(),
                lunchMatchDTO.getAddress()
        );
        if (existingMatch.isPresent()) {
            log.warn("이미 존재하는 맛집입니다: " + lunchMatchDTO.getName());
            return null;
        }
        LunchMatch lunchMatch = modelMapper.map(lunchMatchDTO, LunchMatch.class);
        LunchMatch savedLunchMatch = lunchMatchRepository.save(lunchMatch);
        return savedLunchMatch.getRno();
    }

    @Override
    public LunchMatchDTO getOne(Long rno) {
        Optional<LunchMatch> result = lunchMatchRepository.findById(rno);
        LunchMatch lunchMatch = result.orElseThrow();
        return modelMapper.map(lunchMatch, LunchMatchDTO.class);
    }

    // --- getAll() 메서드에서 ModelMapper 대신 수동 변환으로 변경 ---
    @Override
    public List<LunchMatchDTO> getAll() {
        List<LunchMatch> lunchMatches = lunchMatchRepository.findAll();
        List<LunchMatchDTO> dtoList = new ArrayList<>();
        for (LunchMatch lunchMatch : lunchMatches) {
            try {
                // 수동으로 필드 복사하여 DTO 생성 (ModelMapper의 잠재적 오류 우회)
                LunchMatchDTO dto = LunchMatchDTO.builder()
                        .rno(lunchMatch.getRno())
                        .name(lunchMatch.getName())
                        .address(lunchMatch.getAddress())
                        .phoneNumber(lunchMatch.getPhoneNumber())
                        .category(lunchMatch.getCategory())
                        .latitude(lunchMatch.getLatitude())
                        .longitude(lunchMatch.getLongitude())
                        .rating(lunchMatch.getRating())
                        .priceLevel(lunchMatch.getPriceLevel())
                        .operatingHours(lunchMatch.getOperatingHours())
                        .build();
                dtoList.add(dto);
            } catch (Exception e) {
                log.error("getAll()에서 맛집 엔티티 -> DTO 변환 중 오류 발생 (rno: {}): {}", lunchMatch.getRno(), e.getMessage());
            }
        }
        log.info("getAll() 결과 DTO 수: {}", dtoList.size()); // 로그 추가
        return dtoList;
    }
    // ----------------------------------------------------------------

    @Override
    public void modify(LunchMatchDTO lunchMatchDTO) {
        Optional<LunchMatch> result = lunchMatchRepository.findById(lunchMatchDTO.getRno());
        LunchMatch lunchMatch = result.orElseThrow();
        lunchMatch.change(
                lunchMatchDTO.getName(),
                lunchMatchDTO.getAddress(),
                lunchMatchDTO.getPhoneNumber(),
                lunchMatchDTO.getCategory(),
                lunchMatchDTO.getLatitude(),
                lunchMatchDTO.getLongitude(),
                lunchMatchDTO.getRating(),
                lunchMatchDTO.getPriceLevel(),
                lunchMatchDTO.getOperatingHours()
        );
        lunchMatchRepository.save(lunchMatch);
    }

    @Override
    public void remove(Long rno) {
        lunchMatchRepository.deleteById(rno);
    }

    // --- searchAndSort() 메서드에서 ModelMapper 대신 수동 변환으로 변경 ---
    @Override
    public List<LunchMatchDTO> searchAndSort(String keyword, String category, Double minRating, String orderBy) {
        log.info("searchAndSort 서비스 실행: keyword={}, category={}, minRating={}, orderBy={}", keyword, category, minRating, orderBy);

        List<String> keywordsForDbSearch = new ArrayList<>();

        if (StringUtils.hasText(keyword)) {
            KakaoLocalSearchResponse kakaoResponse = kakaoApiService.searchPlacesByKeyword(keyword, 1);

            if (kakaoResponse != null && kakaoResponse.getDocuments() != null && !kakaoResponse.getDocuments().isEmpty()) {
                log.info("카카오 API에서 {}개 장소 검색됨", kakaoResponse.getDocuments().size());

                for (KakaoLocalSearchResponse.Document doc : kakaoResponse.getDocuments()) {
                    if (StringUtils.hasText(doc.getRoadAddressName())) {
                        keywordsForDbSearch.add(doc.getRoadAddressName());
                    }
                    if (StringUtils.hasText(doc.getAddressName())) {
                        keywordsForDbSearch.add(doc.getAddressName());
                    }
                    if (StringUtils.hasText(doc.getPlaceName())) {
                        keywordsForDbSearch.add(doc.getPlaceName());
                    }
                }
                if (!keywordsForDbSearch.contains(keyword)) {
                    keywordsForDbSearch.add(keyword);
                }

                log.info("카카오 API로부터 추출된 DB 검색 키워드 목록: {}", keywordsForDbSearch);

            } else {
                log.warn("카카오 API에서 검색어 '{}'에 대한 결과를 찾지 못했습니다. 원본 키워드로 DB 검색을 시도합니다.", keyword);
                keywordsForDbSearch.add(keyword);
            }
        } else {
            keywordsForDbSearch.add("");
        }

        List<LunchMatch> lunchMatches = lunchMatchRepository.search(keywordsForDbSearch, category, minRating, orderBy);

        List<LunchMatchDTO> dtoList = new ArrayList<>(); // 수동 변환을 위한 리스트
        for (LunchMatch lunchMatch : lunchMatches) {
            try {
                // 수동으로 필드 복사하여 DTO 생성 (ModelMapper의 잠재적 오류 우회)
                LunchMatchDTO dto = LunchMatchDTO.builder()
                        .rno(lunchMatch.getRno())
                        .name(lunchMatch.getName())
                        .address(lunchMatch.getAddress())
                        .phoneNumber(lunchMatch.getPhoneNumber())
                        .category(lunchMatch.getCategory())
                        .latitude(lunchMatch.getLatitude())
                        .longitude(lunchMatch.getLongitude())
                        .rating(lunchMatch.getRating())
                        .priceLevel(lunchMatch.getPriceLevel())
                        .operatingHours(lunchMatch.getOperatingHours())
                        .build();
                dtoList.add(dto);
            } catch (Exception e) {
                log.error("searchAndSort()에서 맛집 엔티티 -> DTO 변환 중 오류 발생 (rno: {}): {}", lunchMatch.getRno(), e.getMessage());
            }
        }
        log.info("searchAndSort() 결과 DTO 수: {}", dtoList.size()); // 로그 추가
        return dtoList;
    }
    // --------------------------------------------------------------------------

    @Override
    public List<LunchMatchDTO> getListWithPaging(int limit, int offset) {
        log.warn("getListWithPaging 기능은 아직 구현되지 않습니다.");
        return Collections.emptyList();
    }

    @Override
    public int getTotalCount() {
        return (int) lunchMatchRepository.count();
    }
}