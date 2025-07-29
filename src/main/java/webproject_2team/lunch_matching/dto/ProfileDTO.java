package webproject_2team.lunch_matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO implements Serializable {

    // 클라이언트로부터 직접 받는 파일 데이터
    private MultipartFile file; // 실제 업로드된 파일 데이터를 받기 위한 필드

    // 파일 저장 후 DB에 저장될 정보 (서비스 계층에서 생성)
    private String uuid; // 파일의 고유 식별자 (중복 방지)
    private String fileName; // 원본 파일명
    private String savePath; // 파일이 저장된 서버의 경로 (예: /upload/profile/)
    private String contentType; // 파일의 MIME 타입 (예: image/jpeg)
    private long fileSize; // 파일 크기 (바이트 단위)
    private boolean img; // 이미지 파일 여부 (썸네일 생성 등에 활용)

    // 파일 전체 경로를 쉽게 얻기 위한 메서드
    public String getFullPath() {
        if (savePath == null || uuid == null || fileName == null) {
            return null;
        }
        return savePath + "/" + uuid + "_" + fileName;
    }

    // 썸네일 경로를 얻기 위한 메서드 (이미지인 경우)
    public String getThumbnailPath() {
        if (!img || savePath == null || uuid == null || fileName == null) {
            return null;
        }
        return savePath + "/s_" + uuid + "_" + fileName; // 썸네일 파일명은 's_' 접두사 사용
    }
}

