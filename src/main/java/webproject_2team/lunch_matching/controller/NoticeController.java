package webproject_2team.lunch_matching.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import webproject_2team.lunch_matching.dto.notice.NoticeDTO;
import webproject_2team.lunch_matching.service.notice.NoticeService;

import java.util.List;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 페이지
    @GetMapping("/list")
    public String list(Model model) {
        List<NoticeDTO> notices = noticeService.getList();
        model.addAttribute("notices", notices);
        return "notice/list"; // templates/notice/list.html 파일을 보여줌
    }

    // 공지사항 상세 보기 페이지
    @GetMapping("/read/{id}")
    public String read(@PathVariable("id") Long id, Model model) {
        NoticeDTO noticeDTO = noticeService.read(id);
        model.addAttribute("dto", noticeDTO);
        return "notice/read"; // templates/notice/read.html 파일을 보여줌
    }
    // NoticeController.java 안에 추가

    // 글쓰기 폼 페이지를 여는 메소드
    @GetMapping("/write")
    public String writeForm() {
        return "notice/write"; // notice/write.html 파일을 보여줌
    }

    // 작성된 폼 데이터를 처리하는 메소드
    @PostMapping("/write")
    public String writePost(NoticeDTO noticeDTO) {
        noticeService.register(noticeDTO);
        return "redirect:/notice/list"; // 글 등록 후 목록으로 이동
    }

    // 수정 폼 페이지를 여는 메소드
    @GetMapping("/modify/{id}")
    public String modifyForm(@PathVariable("id") Long id, Model model) {
        // 기존 데이터를 불러와서 화면에 전달
        NoticeDTO noticeDTO = noticeService.read(id);
        model.addAttribute("dto", noticeDTO);
        return "notice/modify";
    }

    // 수정 데이터를 처리하는 메소드
    @PostMapping("/modify")
    public String modifyPost(NoticeDTO noticeDTO) {
        noticeService.update(noticeDTO);
        // 수정 후에는 해당 글의 상세 보기 페이지로 이동
        return "redirect:/notice/read/" + noticeDTO.getId();
    }

    // 삭제를 처리하는 메소드
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        noticeService.delete(id);
        // 삭제 후에는 목록으로 이동
        return "redirect:/notice/list";
    }
}