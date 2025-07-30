package webproject_2team.lunch_matching.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable // 다른 엔티티에 포함될 수 있는 클래스
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResult {
    private String uuid;
    private String fileName;
    private boolean isImage;

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }
}
