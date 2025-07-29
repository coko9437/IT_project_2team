package webproject_2team.lunch_matching.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webproject_2team.lunch_matching.domain.CommentEntity;
import webproject_2team.lunch_matching.domain.PartyBoardEntity;
import webproject_2team.lunch_matching.service.CommentService;
import webproject_2team.lunch_matching.repository.PartyBoardRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    private final CommentService       commentService;
    private final PartyBoardRepository partyBoardRepository;

    // ── 댓글 등록 ──
    @PostMapping("/add")
    public String add(@RequestParam Long partyId,
                      @RequestParam String content,
                      HttpSession session,
                      Model model) {

        log.info("[Controller] POST /comment/add 호출 partyId={}, content={}", partyId, content);

        String writerId = (String) session.getAttribute("loginId");
        String gender   = (String) session.getAttribute("gender");
        log.info("[Controller] 세션 writerId={}, gender={}", writerId, gender);

        // ↓↓↓ 세션 체크 임시 우회 or 기본값 할당 ↓↓↓
        if (writerId == null) {
            writerId = "testUser";    // 테스트용 임시 아이디
            log.info("[Controller] writerId가 null이라 기본값(testUser)으로 설정");
        }
        if (gender == null) {
            gender = "MALE";          // 테스트용 임시 성별
            log.info("[Controller] gender가 null이라 기본값(MALE)으로 설정");
        }
        // ↑↑↑ 여기를 지우거나 나중에 로그인 구현되면 다시 체크 로직으로 대체 ↑↑↑

        // 2) 파티 조회
        PartyBoardEntity party = partyBoardRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 파티가 없습니다."));

        // 3) 성별 제한 체크
        String limit = party.getGenderLimit();
        if (!"ALL".equalsIgnoreCase(limit) && !limit.equalsIgnoreCase(gender)) {
            model.addAttribute("party",       party);
            model.addAttribute("commentList", commentService.getCommentsByPartyId(partyId));
            model.addAttribute("error",       "성별 제한으로 댓글을 작성할 수 없습니다.");
            return "party/party_read";
        }

        // 4) 저장
        CommentEntity comment = CommentEntity.builder()
                .partyId(partyId)
                .content(content)
                .writerId(writerId)
                .gender(gender)
                .build();
        log.info("[Controller] 서비스에 위임 -> addComment");
        commentService.addComment(comment);
        log.info("[Controller] 댓글 저장 완료");

        return "redirect:/party/read?id=" + partyId;
    }


    // ── 댓글 수정 ──
    @PostMapping("/update")
    public String update(@RequestParam Long id,
                         @RequestParam Long partyId,
                         @RequestParam String content,
                         HttpSession session) {
        // (로그인/성별 체크 원하면 위와 동일하게 추가)
        CommentEntity c = CommentEntity.builder()
                .id(id)
                .partyId(partyId)
                .content(content)
                // writerId, gender는 수정할 때 변경 안 하니 세션에서 꺼내든 생략하든 무방
                .build();
        commentService.updateComment(c);
        return "redirect:/party/read?id=" + partyId;
    }

    // ── 댓글 삭제 ──
    @PostMapping("/delete")
    public String delete(@RequestParam Long id,
                         @RequestParam Long partyId,
                         HttpSession session) {
        // (로그인/성별 체크 원하면 추가)
        commentService.deleteComment(id);
        return "redirect:/party/read?id=" + partyId;
    }

}
