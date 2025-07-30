package webproject_2team.lunch_matching.controller;

import webproject_2team.lunch_matching.util.UploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@Log4j2
public class FileController {

    private final UploadUtil uploadUtil;

    @GetMapping("/view/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {
        try {
            byte[] data = uploadUtil.getFile(fileName);
            String contentType = Files.probeContentType(Paths.get(fileName));

            if (contentType == null) {
                // 기본값 설정 또는 에러 처리
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(data);
        } catch (Exception e) {
            log.error("파일 조회 실패: " + fileName, e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/removeFile/{fileName}")
    public ResponseEntity<String> removeFile(@PathVariable String fileName) {
        try {
            uploadUtil.deleteFile(fileName);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            log.error("파일 삭제 실패: " + fileName, e);
            return ResponseEntity.status(500).body("Failed to delete file");
        }
    }
}
