package webproject_2team.lunch_matching.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webproject_2team.lunch_matching.dto.InquiryDTO;
import webproject_2team.lunch_matching.service.InquiryService;

import java.util.List;

@Controller
@RequestMapping("/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // 내 문의 목록 페이지
    @GetMapping("/list")
    public String list(Model model) {
        // TODO: 추후 스프링 시큐리티로 로그인한 사용자 정보를 가져와야 합니다.
        String currentUsername = "user1";

        List<InquiryDTO> inquiries = inquiryService.getListOfWriter(currentUsername);
        model.addAttribute("inquiries", inquiries);

        return "inquiry/inquiry_list"; // inquiry_list.html 뷰를 반환
    }

    // 문의 등록 폼 페이지
    @GetMapping("/ask")
    public String askForm() { // 1. 반환 타입을 String으로 변경
        return "inquiry/inquiry_form"; // 2. inquiry_form.html 뷰를 명시적으로 반환
    }

    // 문의 등록 처리
    @PostMapping("/ask")
    public String askPost(InquiryDTO inquiryDTO, RedirectAttributes redirectAttributes) {
        // TODO: 추후 스프링 시큐리티로 로그인한 사용자 정보를 설정해야 합니다.
        inquiryDTO.setWriter("user1");

        inquiryService.register(inquiryDTO);

        redirectAttributes.addFlashAttribute("message", "문의가 성공적으로 등록되었습니다.");
        return "redirect:/inquiry/list";
    }

    // 문의 상세 보기 페이지
    @GetMapping("/read/{id}")
    public String read(@PathVariable("id") Long id, Model model) {
        InquiryDTO inquiryDTO = inquiryService.read(id);
        model.addAttribute("dto", inquiryDTO);

        // TODO: 본인 글 또는 관리자만 볼 수 있도록 접근 제어 로직이 필요합니다.

        return "inquiry/inquiry_read"; // inquiry_read.html 뷰를 반환
    }
}