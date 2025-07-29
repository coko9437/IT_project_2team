package webproject_2team.lunch_matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {
    private String uuid;
    private String fileName;
    private boolean img;

    public String getLink() {
        if (img) {
            return uuid + "_" + fileName;
        } else {
            return "default.png";
        }
    }

    public String getThumbnailLink() {
        if (img) {
            return "s_" + uuid + "_" + fileName;
        } else {
            // 이미지가 아닐 경우 기본 이미지 또는 다른 처리를 할 수 있습니다.
            return "default.png"; // 기본 이미지 경로 반환
        }
    }
}
