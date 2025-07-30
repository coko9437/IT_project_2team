package webproject_2team.lunch_matching.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * ModelMapper에 리뷰 관련 매핑 설정을 추가하는 클래스입니다.
 * 이전에는 이 클래스에서 ModelMapper 빈을 직접 생성하여 중복 빈 정의 문제가 발생했습니다.
 * 이제는 ModelMapperConfig에서 생성된 전역 ModelMapper 빈을 주입받아(DI) 사용합니다.
 * @PostConstruct 어노테이션을 사용하여, 이 클래스가 초기화된 후(의존성 주입이 완료된 후)
 * 주입받은 ModelMapper 인스턴스에 리뷰 관련 매핑 구성을 추가합니다.
 * 이를 통해 ModelMapper 빈의 중복 생성을 방지하고 설정의 중앙 관리를 유지합니다.
 */
@Configuration
@RequiredArgsConstructor
public class ReviewConfig {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureModelMapper() {
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
    }
}
