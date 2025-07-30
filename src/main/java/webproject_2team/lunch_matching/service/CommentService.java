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
}