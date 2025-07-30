package webproject_2team.lunch_matching.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webproject_2team.lunch_matching.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId ORDER BY c.createdAt ASC")
    List<Comment> findByBoardIdOrderByCreatedAtAsc(@Param("boardId") Long boardId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.board.id = :boardId")
    long countByBoardId(@Param("boardId") Long boardId);
}