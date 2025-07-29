package webproject_2team.lunch_matching.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webproject_2team.lunch_matching.domain.Board;
import webproject_2team.lunch_matching.domain.Comment;
import webproject_2team.lunch_matching.dto.PageRequestDTO;
import webproject_2team.lunch_matching.dto.PageResponseDTO;
import webproject_2team.lunch_matching.repository.BoardRepository;
import webproject_2team.lunch_matching.service.BoardService;
import webproject_2team.lunch_matching.service.CommentService;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/board/list")
    public String list(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<Board> responseDTO = boardService.getBoardList(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        return "board_list";
    }

    @GetMapping("/board/read")
    public String read(@RequestParam("id") Long id,
                       @RequestParam(value = "userGender", required = false, defaultValue = "성별상관무") String userGender,
                       @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                       @RequestParam(value = "type", required = false) String type,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "genderFilter", required = false) String genderFilter,
                       @RequestParam(value = "foodFilter", required = false) String foodFilter,
                       Model model) {
        Board board = boardService.read(id);
        if (board != null) {
            // 성별 접근 권한 확인
            if (!boardService.canAccessBoard(board, userGender)) {
                model.addAttribute("error", "접근 권한이 없습니다. (" + board.getGenderLimit() + " 전용)");
                return "access_denied";
            }

            // 댓글 목록 조회
            List<Comment> comments = commentService.getCommentsByBoardId(id);

            model.addAttribute("board", board);
            model.addAttribute("comments", comments);
            model.addAttribute("userGender", userGender);

            // 페이지 정보 추가
            model.addAttribute("page", page);
            model.addAttribute("type", type);
            model.addAttribute("keyword", keyword);
            model.addAttribute("genderFilter", genderFilter);
            model.addAttribute("foodFilter", foodFilter);

            return "read";
        } else {
            return "redirect:/board/list";
        }
    }

    // REST API 방식으로 댓글 작성
    @PostMapping("/api/board/comment")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCommentApi(@RequestParam("boardId") Long boardId,
                                                             @RequestParam("content") String content,
                                                             @RequestParam("writer") String writer) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 댓글 내용 바이트 수 검증 (100바이트)
            if (content != null && content.getBytes("UTF-8").length > 100) {
                response.put("success", false);
                response.put("message", "댓글은 100바이트를 초과할 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            // 필수 필드 검증
            if (content == null || content.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "댓글 내용을 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }

            if (writer == null || writer.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "작성자를 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }

            // 댓글 저장
            Comment comment = commentService.saveComment(boardId, content, writer);

            // 성공 응답 데이터 구성
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("id", comment.getId());
            commentData.put("content", comment.getContent());
            commentData.put("writer", comment.getWriter());
            commentData.put("createdAt", comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            response.put("success", true);
            response.put("message", "댓글이 성공적으로 작성되었습니다.");
            response.put("comment", commentData);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (UnsupportedEncodingException e) {
            response.put("success", false);
            response.put("message", "댓글 처리 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // REST API 방식으로 댓글 삭제
    @DeleteMapping("/api/board/comment/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCommentApi(@PathVariable("id") Long commentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            commentService.deleteComment(commentId);
            response.put("success", true);
            response.put("message", "댓글이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "댓글 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 기존 댓글 작성 메서드 (하위 호환용)
    @PostMapping("/board/comment")
    public String addComment(@RequestParam("boardId") Long boardId,
                             @RequestParam("content") String content,
                             @RequestParam("writer") String writer,
                             @RequestParam(value = "userGender", required = false, defaultValue = "성별상관무") String userGender,
                             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                             @RequestParam(value = "type", required = false) String type,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "genderFilter", required = false) String genderFilter,
                             @RequestParam(value = "foodFilter", required = false) String foodFilter,
                             Model model) {
        try {
            // 댓글 내용 바이트 수 검증 (100바이트)
            if (content != null && content.getBytes("UTF-8").length > 100) {
                model.addAttribute("commentError", "댓글은 100바이트를 초과할 수 없습니다.");
                return read(boardId, userGender, page, type, keyword, genderFilter, foodFilter, model);
            }

            // 필수 필드 검증
            if (content == null || content.trim().isEmpty()) {
                model.addAttribute("commentError", "댓글 내용을 입력해주세요.");
                return read(boardId, userGender, page, type, keyword, genderFilter, foodFilter, model);
            }

            if (writer == null || writer.trim().isEmpty()) {
                model.addAttribute("commentError", "작성자를 입력해주세요.");
                return read(boardId, userGender, page, type, keyword, genderFilter, foodFilter, model);
            }

            // 댓글 저장
            commentService.saveComment(boardId, content, writer);

            // 성공적으로 저장된 후 리다이렉트 (페이지 정보 포함)
            StringBuilder redirectUrl = new StringBuilder("/board/read?id=" + boardId);
            redirectUrl.append("&userGender=").append(URLEncoder.encode(userGender, "UTF-8"));
            redirectUrl.append("&page=").append(page);

            if (type != null && !type.trim().isEmpty()) {
                redirectUrl.append("&type=").append(URLEncoder.encode(type, "UTF-8"));
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                redirectUrl.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
            }
            if (genderFilter != null && !genderFilter.trim().isEmpty()) {
                redirectUrl.append("&genderFilter=").append(URLEncoder.encode(genderFilter, "UTF-8"));
            }
            if (foodFilter != null && !foodFilter.trim().isEmpty()) {
                redirectUrl.append("&foodFilter=").append(URLEncoder.encode(foodFilter, "UTF-8"));
            }

            return "redirect:" + redirectUrl.toString();

        } catch (IllegalStateException e) {
            model.addAttribute("commentError", e.getMessage());
            return read(boardId, userGender, page, type, keyword, genderFilter, foodFilter, model);
        } catch (UnsupportedEncodingException e) {
            model.addAttribute("commentError", "댓글 처리 중 오류가 발생했습니다.");
            return read(boardId, userGender, page, type, keyword, genderFilter, foodFilter, model);
        }
    }

    @PostMapping("/board/comment/delete/{id}")
    public String deleteComment(@PathVariable("id") Long commentId,
                                @RequestParam("boardId") Long boardId,
                                @RequestParam(value = "userGender", required = false, defaultValue = "성별상관무") String userGender,
                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value = "genderFilter", required = false) String genderFilter,
                                @RequestParam(value = "foodFilter", required = false) String foodFilter) {
        commentService.deleteComment(commentId);

        try {
            StringBuilder redirectUrl = new StringBuilder("/board/read?id=" + boardId);
            redirectUrl.append("&userGender=").append(URLEncoder.encode(userGender, "UTF-8"));
            redirectUrl.append("&page=").append(page);

            if (type != null && !type.trim().isEmpty()) {
                redirectUrl.append("&type=").append(URLEncoder.encode(type, "UTF-8"));
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                redirectUrl.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
            }
            if (genderFilter != null && !genderFilter.trim().isEmpty()) {
                redirectUrl.append("&genderFilter=").append(URLEncoder.encode(genderFilter, "UTF-8"));
            }
            if (foodFilter != null && !foodFilter.trim().isEmpty()) {
                redirectUrl.append("&foodFilter=").append(URLEncoder.encode(foodFilter, "UTF-8"));
            }

            return "redirect:" + redirectUrl.toString();
        } catch (Exception e) {
            return "redirect:/board/read?id=" + boardId + "&userGender=" + userGender + "&page=" + page;
        }
    }

    @GetMapping("/board/register")
    public String registerForm() {
        return "board_register";
    }

    @PostMapping("/board/register")
    public String register(Board board, Model model) throws IOException {

        // 제목 글자수 검증 (100바이트)
        if (board.getTitle() != null && board.getTitle().getBytes("UTF-8").length > 100) {
            model.addAttribute("titleError", "제목은 100바이트를 초과할 수 없습니다.");
            return "board_register";
        }

        // 본문 글자수 검증 (2000바이트)
        if (board.getContent() != null && board.getContent().getBytes("UTF-8").length > 2000) {
            model.addAttribute("contentError", "본문은 2000바이트를 초과할 수 없습니다.");
            return "board_register";
        }

        // 필수 필드 검증
        if (board.getTitle() == null || board.getTitle().trim().isEmpty()) {
            model.addAttribute("titleError", "제목을 입력해주세요.");
            return "board_register";
        }

        if (board.getContent() == null || board.getContent().trim().isEmpty()) {
            model.addAttribute("contentError", "본문을 입력해주세요.");
            return "board_register";
        }

        if (board.getWriter() == null || board.getWriter().trim().isEmpty()) {
            model.addAttribute("writerError", "작성자를 입력해주세요.");
            return "board_register";
        }

        if (board.getRegion() == null || board.getRegion().trim().isEmpty()) {
            model.addAttribute("regionError", "지역을 입력해주세요.");
            return "board_register";
        }

        board.setCreatedAt(LocalDateTime.now());
        boardService.save(board);
        return "redirect:/board/list";
    }

    @PostMapping("/board/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        boardService.delete(id);
        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String modifyForm(@PathVariable("id") Long id, Model model) {
        Board board = boardService.read(id);
        model.addAttribute("board", board);
        return "board_modify";
    }

    @PostMapping("/board/modify")
    public String modify(Board board, Model model) throws IOException {

        // 제목 글자수 검증 (100바이트)
        if (board.getTitle() != null && board.getTitle().getBytes("UTF-8").length > 100) {
            model.addAttribute("titleError", "제목은 100바이트를 초과할 수 없습니다.");
            return "board_modify";
        }

        // 본문 글자수 검증 (2000바이트)
        if (board.getContent() != null && board.getContent().getBytes("UTF-8").length > 2000) {
            model.addAttribute("contentError", "본문은 2000바이트를 초과할 수 없습니다.");
            return "board_modify";
        }

        // 필수 필드 검증
        if (board.getTitle() == null || board.getTitle().trim().isEmpty()) {
            model.addAttribute("titleError", "제목을 입력해주세요.");
            return "board_modify";
        }

        if (board.getContent() == null || board.getContent().trim().isEmpty()) {
            model.addAttribute("contentError", "본문을 입력해주세요.");
            return "board_modify";
        }

        if (board.getRegion() == null || board.getRegion().trim().isEmpty()) {
            model.addAttribute("regionError", "지역을 입력해주세요.");
            return "board_modify";
        }

        boardService.modify(board);
        return "redirect:/board/read?id=" + board.getId();
    }
}