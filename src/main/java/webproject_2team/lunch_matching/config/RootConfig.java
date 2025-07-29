package webproject_2team.lunch_matching.config;

import webproject_2team.lunch_matching.domain.UploadResult;
import webproject_2team.lunch_matching.dto.UploadResultDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // UploadResult.isImage -> UploadResultDTO.img 매핑 설정
        modelMapper.createTypeMap(webproject_2team.lunch_matching.domain.UploadResult.class, webproject_2team.lunch_matching.dto.UploadResultDTO.class)
                .addMapping(UploadResult::isImage, UploadResultDTO::setImg);

        // UploadResultDTO.img -> UploadResult.isImage 매핑 설정
        modelMapper.createTypeMap(webproject_2team.lunch_matching.dto.UploadResultDTO.class, webproject_2team.lunch_matching.domain.UploadResult.class)
                .addMapping(UploadResultDTO::isImg, UploadResult::setIsImage);

        modelMapper.createTypeMap(webproject_2team.lunch_matching.domain.Review.class, webproject_2team.lunch_matching.dto.ReviewDTO.class)
                .addMapping(webproject_2team.lunch_matching.domain.Review::getFileList, webproject_2team.lunch_matching.dto.ReviewDTO::setUploadFileNames);

        return modelMapper;
    }
}