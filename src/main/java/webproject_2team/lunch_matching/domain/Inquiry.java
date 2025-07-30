package webproject_2team.lunch_matching.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob // TEXT 타입으로 생성
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String writer;

    private LocalDateTime createdAt;

    @Lob
    private String answer; // 관리자 답변

    private LocalDateTime answeredAt; // 답변 일시

    private boolean isAnswered; // 답변 여부 (true/false)

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isAnswered = false;
    }
}