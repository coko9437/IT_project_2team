package webproject_2team.lunch_matching.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import webproject_2team.lunch_matching.domain.Review;
import webproject_2team.lunch_matching.dto.PageRequestDTO;
import webproject_2team.lunch_matching.dto.PageResponseDTO;
import webproject_2team.lunch_matching.dto.ReviewDTO;
import webproject_2team.lunch_matching.repository.ReviewRepository;
import webproject_2team.lunch_matching.util.UploadUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.context.event.EventListener;
import webproject_2team.lunch_matching.event.ReviewLikeUpdateEvent;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {

    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;
    private final UploadUtil uploadUtil;
    private final ReviewLikeService reviewLikeService;

    @Override
    public Long register(ReviewDTO reviewDTO) {
        log.info("register...");
        Review review = modelMapper.map(reviewDTO, Review.class);

        // ModelMapper가 이미 UploadResultDTO 리스트를 Review 엔티티의 fileList에 매핑합니다.
        // 따라서 아래의 수동 추가 로직은 제거합니다.
        // if (reviewDTO.getUploadFileNames() != null && !reviewDTO.getUploadFileNames().isEmpty()) {
        //     reviewDTO.getUploadFileNames().forEach(uploadResultDTO -> {
        //         review.getFileList().add(modelMapper.map(uploadResultDTO, webproject_2team.lunch_matching.domain.UploadResult.class));
        //     });
        // }

        Long review_id = reviewRepository.save(review).getReview_id();

        log.info("Review entity before saving: " + review);
        if (review.getFileList() != null) {
            review.getFileList().forEach(file -> log.info("  File in review entity: " + file.getFileName() + ", isImage: " + file.isImage()));
        }

        return review_id;
    }

    @Override
    public ReviewDTO readOne(Long review_id) {
        java.util.Optional<Review> result = reviewRepository.findByIdWithFiles(review_id);
        Review review = result.orElseThrow();

        // Lazy-loaded 컬렉션들을 명시적으로 초기화
        Hibernate.initialize(review.getFileList());
        Hibernate.initialize(review.getComments());
        Hibernate.initialize(review.getLikes());

        log.info("Review entity likeCount before mapping: " + review.getLikeCount());

        // Add log for fileList content
        if (review.getFileList() != null) {
            log.info("Review entity fileList size after initialization: " + review.getFileList().size());
            review.getFileList().forEach(file -> log.info("  File in review entity (after init): " + file.getFileName() + ", isImage: " + file.isImage() + ", UUID: " + file.getUuid()));
        } else {
            log.info("Review entity fileList is null after initialization.");
        }

        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);

        // TODO: 실제 사용자 ID는 Spring Security 등 인증 시스템에서 가져와야 합니다.
        String memberId = "testuser"; // 임시 사용자 ID
        boolean liked = reviewLikeService.isLiked(review_id, memberId);
        reviewDTO.setLikedByCurrentUser(liked);

        return reviewDTO;
    }

    @Transactional
    @Override
    public void modify(ReviewDTO reviewDTO) {
        java.util.Optional<Review> result = reviewRepository.findByIdWithFiles(reviewDTO.getReview_id()); // Fetch with files
        Review review = result.orElseThrow();

        review.change(reviewDTO.getContent(), reviewDTO.getMenu(), reviewDTO.getPlace(), reviewDTO.getRating(), reviewDTO.getEmotion());

        // --- File List Management ---
        // Get current files associated with the review from the database
        List<webproject_2team.lunch_matching.domain.UploadResult> existingFiles = new java.util.ArrayList<>(review.getFileList());

        // Create sets for efficient lookup of existing file UUIDs
        java.util.Set<String> existingFileUuids = existingFiles.stream()
                .map(webproject_2team.lunch_matching.domain.UploadResult::getUuid)
                .collect(Collectors.toSet());

        // Only process file changes if reviewDTO.getUploadFileNames() is not null.
        // If it's null, it means the file input was not touched, so preserve existing files.
        if (reviewDTO.getUploadFileNames() != null) {
            List<webproject_2team.lunch_matching.dto.UploadResultDTO> incomingFilesDTO = reviewDTO.getUploadFileNames();

            // Create sets for efficient lookup of incoming file UUIDs
            java.util.Set<String> incomingFileUuids = incomingFilesDTO.stream()
                    .map(webproject_2team.lunch_matching.dto.UploadResultDTO::getUuid)
                    .collect(Collectors.toSet());

            // Identify files to remove (exist in DB but not in incoming DTO)
            java.util.List<webproject_2team.lunch_matching.domain.UploadResult> filesToRemove = existingFiles.stream()
                    .filter(file -> !incomingFileUuids.contains(file.getUuid()))
                    .collect(Collectors.toList());

            // Identify files to add (in incoming DTO but not in DB)
            java.util.List<webproject_2team.lunch_matching.dto.UploadResultDTO> filesToAddDTO = incomingFilesDTO.stream()
                    .filter(fileDTO -> !existingFileUuids.contains(fileDTO.getUuid()))
                    .collect(Collectors.toList());

            // Remove files from the review's fileList and MinIO
            filesToRemove.forEach(file -> {
                review.getFileList().remove(file); // This will trigger deletion from DB if orphanRemoval=true
                String originalObjectKey = file.getUuid() + "_" + file.getFileName();
                deleteFileAsync(originalObjectKey);
                if (file.isImage()) {
                    String thumbnailObjectKey = "s_" + originalObjectKey;
                    deleteFileAsync(thumbnailObjectKey);
                }
            });

            // Add new files to the review's fileList
            filesToAddDTO.forEach(fileDTO -> {
                review.getFileList().add(modelMapper.map(fileDTO, webproject_2team.lunch_matching.domain.UploadResult.class));
            });
        }
        // --- End File List Management ---

        reviewRepository.save(review);
    }

    @Transactional
    @Override
    public void remove(Long review_id) {
        java.util.Optional<Review> result = reviewRepository.findByIdWithFiles(review_id);
        Review review = result.orElseThrow();

        // MinIO에서 파일 삭제
        if (review.getFileList() != null && !review.getFileList().isEmpty()) {
            review.getFileList().forEach(file -> {
                String originalObjectKey = file.getUuid() + "_" + file.getFileName();
                deleteFileAsync(originalObjectKey);
                if (file.isImage()) {
                    String thumbnailObjectKey = "s_" + originalObjectKey;
                    deleteFileAsync(thumbnailObjectKey);
                }
            });
        }

        reviewRepository.deleteById(review_id);
    }

    @Async
    public void deleteFileAsync(String objectKey) {
        try {
            uploadUtil.deleteFile(objectKey);
            log.info("Async delete successful for: " + objectKey);
        } catch (Exception e) {
            log.error("Async delete failed for: " + objectKey, e);
        }
    }

    @Transactional
    @Override
    public PageResponseDTO<ReviewDTO> getList(PageRequestDTO pageRequestDTO) {
        Page<Review> result = reviewRepository.searchAll(pageRequestDTO, pageRequestDTO.getPageable(Sort.by("review_id").descending()));

        List<ReviewDTO> dtoList = result.getContent().stream()
                .map(review -> {
                    // Initialize lazy-loaded collections before mapping
                    Hibernate.initialize(review.getFileList()); // Force initialization of the lazy-loaded collection
                    Hibernate.initialize(review.getComments()); // Initialize comments
                    Hibernate.initialize(review.getLikes());    // Initialize likes

                    log.info("--- Debug: Review entity fileList before mapping ---");
                    if (review.getFileList() != null) {
                        log.info("  Review ID: " + review.getReview_id() + ", FileList Size: " + review.getFileList().size());
                        review.getFileList().forEach(file -> log.info("  File: " + file.getFileName() + ", isImage: " + file.isImage()));
                    } else {
                        log.info("  Review ID: " + review.getReview_id() + ", fileList is null");
                    }

                    ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
                    if (review.getFileList() != null) {
                        reviewDTO.setUploadFileNames(review.getFileList().stream()
                                .map(file -> modelMapper.map(file, webproject_2team.lunch_matching.dto.UploadResultDTO.class))
                                .collect(Collectors.toList()));
                    }
                    return reviewDTO;
                })
                .collect(Collectors.toList());

        log.info("PageRequestDTO size: " + pageRequestDTO.getSize());
        log.info("Total elements from result: " + result.getTotalElements());

        return PageResponseDTO.<ReviewDTO>builder()
                .dtoList(dtoList)
                .totalCount((int)result.getTotalElements())
                .page(pageRequestDTO.getPage()) // pageRequestDTO에서 page 가져오기
                .size(pageRequestDTO.getSize()) // pageRequestDTO에서 size 가져오기
                .build();
    }

    @Transactional
    @Override
    public void updateLikeCount(Long reviewId, int likeCount) {
        reviewRepository.findById(reviewId).ifPresent(review -> {
            review.setLikeCount(likeCount);
            reviewRepository.save(review);
        });
    }

    @EventListener
    @Transactional
    public void handleReviewLikeUpdateEvent(ReviewLikeUpdateEvent event) {
        log.info("Handling ReviewLikeUpdateEvent for review ID: {} with like count: {}", event.getReviewId(), event.getLikeCount());
        reviewRepository.findById(event.getReviewId()).ifPresent(review -> {
            review.setLikeCount(event.getLikeCount());
            reviewRepository.save(review);
            log.info("Review likeCount updated in DB for review ID: {}", event.getReviewId());
        });
    }
}


