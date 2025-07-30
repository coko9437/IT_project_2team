package webproject_2team.lunch_matching.controller;

import webproject_2team.lunch_matching.dto.LunchMatchDTO;
import webproject_2team.lunch_matching.service.LunchMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value; // @Value 임포트 추가
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/lunchmatch")
@RequiredArgsConstructor
@Log4j2
public class LunchMatchViewController {

    private final LunchMatchService lunchMatchService;

    // application-secret.properties에서 JavaScript API 키 주입
    @Value("${kakao.javascript.api.key}")
    private String kakaoJavascriptApiKey;

    // 이 컨트롤러는 View를 반환하므로, REST API 키는 MapSearchController나 KakaoApiService에서 사용됩니다.
    // 여기서는 필요에 따라 주입받거나, 사용하지 않을 수도 있습니다.
    // @Value("${kakao.rest.api.key}")
    // private String kakaoRestApiKey;


    @GetMapping("/list")
    public String listPage(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            @RequestParam(value = "orderBy", required = false, defaultValue = "latest") String orderBy,
            @RequestParam(value = "minRating", required = false) Double minRating,
            Model model) {

        log.info("저장된 맛집 목록 페이지 요청 (뷰 렌더링용): keyword={}, category={}, orderBy={}, minRating={}", keyword, category, orderBy, minRating);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("minRating", minRating);

        // Thymeleaf 템플릿으로 JavaScript 키 전달
        model.addAttribute("kakaoJsApiKey", kakaoJavascriptApiKey);

        return "lunchmatch/list"; // list.html 뷰 반환
    }

    @GetMapping("/api/list")
    @ResponseBody
    public List<LunchMatchDTO> getLunchMatchesJson(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            @RequestParam(value = "orderBy", required = false, defaultValue = "latest") String orderBy,
            @RequestParam(value = "minRating", required = false) Double minRating) {

        log.info("맛집 목록 데이터 요청 (AJAX API용): keyword={}, category={}, orderBy={}, minRating={}", keyword, category, orderBy, minRating);
        return lunchMatchService.searchAndSort(keyword, category, minRating, orderBy);
    }

    @GetMapping("/read/{rno}")
    public String read(@PathVariable("rno") Long rno, Model model) {
        log.info("맛집 상세 정보 페이지 요청: rno = " + rno);
        LunchMatchDTO lunchMatchDTO = lunchMatchService.getOne(rno);
        model.addAttribute("dto", lunchMatchDTO);
        // Thymeleaf 템플릿으로 JavaScript 키 전달
        model.addAttribute("kakaoJsApiKey", kakaoJavascriptApiKey);
        return "lunchmatch/read";
    }

    @GetMapping("/modify/{rno}")
    public String modifyForm(@PathVariable("rno") Long rno, Model model) {
        log.info("맛집 수정 페이지 요청: rno = " + rno);
        LunchMatchDTO lunchMatchDTO = lunchMatchService.getOne(rno);
        model.addAttribute("dto", lunchMatchDTO);
        // 수정 페이지에도 필요하다면 JavaScript 키 전달 (일반적으로 지도 없으면 필요 없음)
        // model.addAttribute("kakaoJsApiKey", kakaoJavascriptApiKey);
        return "lunchmatch/modify";
    }

    @PostMapping("/modify")
    public String modify(
            @Valid LunchMatchDTO lunchMatchDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("맛집 수정 처리 요청: " + lunchMatchDTO);

        if (bindingResult.hasErrors()) {
            log.error("수정 데이터 유효성 검사 오류: " + bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/lunchmatch/modify/" + lunchMatchDTO.getRno();
        }

        lunchMatchService.modify(lunchMatchDTO);

        return "redirect:/lunchmatch/read/" + lunchMatchDTO.getRno();
    }

    @GetMapping("/map")
    public String mapPage(Model model) {
        log.info("Request for lunchmatch map page...");
        // Thymeleaf 템플릿으로 JavaScript 키 전달
        model.addAttribute("kakaoJsApiKey", kakaoJavascriptApiKey);
        return "lunchmatch/map";
    }
}