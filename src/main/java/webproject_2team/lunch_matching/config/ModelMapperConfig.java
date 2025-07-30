package webproject_2team.lunch_matching.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapper의 설정을 담당하는 클래스입니다.
 * 이 클래스를 통해 ModelMapper를 Spring의 Bean으로 등록합니다.
 */
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD); // <-- 이 부분이 STANDARD인지 확인
        // STRICT에서 STANDARD로 변경 유지

        return modelMapper;
    }
}