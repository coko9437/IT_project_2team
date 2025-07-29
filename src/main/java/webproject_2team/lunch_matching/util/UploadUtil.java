package webproject_2team.lunch_matching.util;

import webproject_2team.lunch_matching.dto.UploadResultDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Log4j2
public class UploadUtil {

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("Upload directory created: " + uploadPath);
            }
        } catch (IOException e) {
            log.error("Failed to create upload directory: " + uploadPath, e);
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Async
    public CompletableFuture<List<UploadResultDTO>> uploadFiles(List<MultipartFile> files) {
        List<UploadResultDTO> resultList = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            if (multipartFile.isEmpty()) {
                continue;
            }

            String originalName = multipartFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedFileName = uuid + "_" + originalName;

            Path savePath = Paths.get(uploadPath, savedFileName);
            boolean isImage = false;

            log.info("File Content Type: " + multipartFile.getContentType()); // 추가된 로그

            try {
                multipartFile.transferTo(savePath); // 원본 파일 저장

                // 이미지 파일 여부 확인 및 썸네일 생성 후 저장
                if (multipartFile.getContentType() != null && multipartFile.getContentType().startsWith("image")) {
                    isImage = true;
                    String thumbnailFileName = "s_" + savedFileName;
                    Path thumbnailSavePath = Paths.get(uploadPath, thumbnailFileName);

                    Thumbnailator.createThumbnail(savePath.toFile(), thumbnailSavePath.toFile(), 200, 200);
                }
            } catch (IOException e) {
                log.error("파일 업로드 중 오류 발생: " + originalName, e);
                // Optionally rethrow or handle more gracefully
            }

            resultList.add(UploadResultDTO.builder()
                    .uuid(uuid)
                    .fileName(originalName)
                    .img(isImage)
                    .build());
        }
        return CompletableFuture.completedFuture(resultList);
    }

    public byte[] getFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadPath, fileName);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("로컬 파일(" + fileName + ") 가져오기 실패: " + e.getMessage(), e);
            throw new RuntimeException("로컬 파일 가져오기 실패", e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path originalFilePath = Paths.get(uploadPath, fileName);
            Files.deleteIfExists(originalFilePath);
            log.info("Deleted original file: " + originalFilePath);

            // 썸네일 파일도 삭제
            String thumbnailFileName = "s_" + fileName;
            Path thumbnailFilePath = Paths.get(uploadPath, thumbnailFileName);
            Files.deleteIfExists(thumbnailFilePath);
            log.info("Deleted thumbnail file: " + thumbnailFilePath);

        } catch (IOException e) {
            log.error("로컬 파일 삭제 실패: " + fileName, e);
        }
    }
}