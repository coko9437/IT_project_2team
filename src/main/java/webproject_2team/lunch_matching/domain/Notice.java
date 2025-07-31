package webproject_2team.lunch_matching.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter // 예제 편의상 Setter 추가, 실제 프로젝트에서는 필요한 경우에만 추가하는 것을 권장
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notice {

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

    // 조회수 필드 추가
    private int viewCount;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.writer = "admin"; // 공지사항 작성자는 'admin'으로 고정
        this.viewCount = 0;
    }
}