package webproject_2team.lunch_matching.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webproject_2team.lunch_matching.domain.Board;
import webproject_2team.lunch_matching.domain.Comment;
import webproject_2team.lunch_matching.repository.BoardRepository;
import webproject_2team.lunch_matching.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public List<Comment> getCommentsByBoardId(Long boardId) {
        return commentRepository.findByBoardIdOrderByCreatedAtAsc(boardId);
    }

    public Comment saveComment(Long boardId, String content, String writer) {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }

        Board board = boardOpt.get();

        // 마감시간 체크
        if (board.isExpired()) {
            throw new IllegalStateException("마감된 게시글에는 댓글을 작성할 수 없습니다.");
        }

        Comment comment = Comment.builder()
                .board(board)
                .content(content)
                .writer(writer)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public long getCommentCount(Long boardId) {
        return commentRepository.countByBoardId(boardId);
    }

    @Transactional
    public Comment updateComment(Long commentId, String newContent) {
        // 1. DB에서 댓글 엔티티를 조회합니다. 없으면 예외를 발생시킵니다.
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다. ID: " + commentId));

        // 2. 개발 단계에서는 권한 확인을 생략합니다.
        //    (추후 로그인 기능 구현 시 주석을 해제하고 사용하세요)
        //    if (!comment.getWriter().equals(loggedInUsername)) {
        //        throw new IllegalAccessException("수정 권한이 없습니다.");
        //    }

        // 3. 댓글의 내용을 새로운 내용으로 변경합니다.
        comment.setContent(newContent);

        // 4. @Transactional에 의해 메소드가 종료될 때 변경된 내용이 자동으로 DB에 반영됩니다.
        return comment;
    }
}