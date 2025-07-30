package webproject_2team.lunch_matching.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD); // <-- 이 부분이 STANDARD인지 확인
        // STRICT에서 STANDARD로 변경 유지

        modelMapper.addMappings(new org.modelmapper.PropertyMap<webproject_2team.lunch_matching.domain.Review, webproject_2team.lunch_matching.dto.ReviewDTO>() {
            @Override
            protected void configure() {
                skip(destination.getComments()); // Review 엔티티의 comments 필드를 ReviewDTO로 매핑할 때 건너뜁니다.
                map(source.getFileList()).setUploadFileNames(null); // Review의 fileList를 ReviewDTO의 uploadFileNames로 매핑
            }
        });

        modelMapper.addMappings(new org.modelmapper.PropertyMap<webproject_2team.lunch_matching.domain.UploadResult, webproject_2team.lunch_matching.dto.UploadResultDTO>() {
            @Override
            protected void configure() {
                map().setImg(source.isImage()); // UploadResult의 isImage를 UploadResultDTO의 img로 매핑
            }
        });

        modelMapper.addMappings(new org.modelmapper.PropertyMap<webproject_2team.lunch_matching.dto.UploadResultDTO, webproject_2team.lunch_matching.domain.UploadResult>() {
            @Override
            protected void configure() {
                map().setIsImage(source.isImg()); // UploadResultDTO의 img를 UploadResult의 isImage로 매핑
            }
        });

        // ReviewComment -> ReviewCommentDTO 매핑 설정 추가
        modelMapper.addMappings(new org.modelmapper.PropertyMap<webproject_2team.lunch_matching.domain.ReviewComment, webproject_2team.lunch_matching.dto.ReviewCommentDTO>() {
            @Override
            protected void configure() {
                map(source.getReview().getReview_id()).setReview_id(null); // review.review_id를 review_id로 매핑
            }
        });

        return modelMapper;
    }
}
