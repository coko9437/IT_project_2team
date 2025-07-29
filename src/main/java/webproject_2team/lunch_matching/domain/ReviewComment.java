package webproject_2team.lunch_matching.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "review") // Review 엔티티와의 무한 루프 방지
public class ReviewComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(length = 200, nullable = false)
    private String member_id; // 댓글 작성자 ID

    @Column(length = 1000, nullable = false)
    private String content; // 댓글 내용

    public void changeContent(String content) {
        this.content = content;
    }

}