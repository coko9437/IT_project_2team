package webproject_2team.lunch_matching.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webproject_2team.lunch_matching.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.title LIKE CONCAT('%',:keyword,'%')")
    Page<Board> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.content LIKE CONCAT('%',:keyword,'%')")
    Page<Board> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.writer LIKE CONCAT('%',:keyword,'%')")
    Page<Board> findByWriterContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "((:title = true AND b.title LIKE CONCAT('%',:keyword,'%')) OR " +
            "(:content = true AND b.content LIKE CONCAT('%',:keyword,'%')) OR " +
            "(:writer = true AND b.writer LIKE CONCAT('%',:keyword,'%')))) " +
            "AND (:genderFilter IS NULL OR :genderFilter = '' OR b.genderLimit = :genderFilter) " +
            "AND (:foodFilter IS NULL OR :foodFilter = '' OR b.foodCategory = :foodFilter)")
    Page<Board> findByKeywordAndTypeAndFilters(@Param("keyword") String keyword,
                                               @Param("title") boolean title,
                                               @Param("content") boolean content,
                                               @Param("writer") boolean writer,
                                               @Param("genderFilter") String genderFilter,
                                               @Param("foodFilter") String foodFilter,
                                               Pageable pageable);

    @Query("SELECT b FROM Board b WHERE " +
            "(:genderFilter IS NULL OR :genderFilter = '' OR b.genderLimit = :genderFilter) " +
            "AND (:foodFilter IS NULL OR :foodFilter = '' OR b.foodCategory = :foodFilter)")
    Page<Board> findByFilters(@Param("genderFilter") String genderFilter,
                              @Param("foodFilter") String foodFilter,
                              Pageable pageable);
}