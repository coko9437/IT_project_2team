package webproject_2team.lunch_matching.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class RootConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                // MatchingStrategies.STRICT는 필드 이름과 타입이 정확히 일치하는 경우에만 매핑
                .setMatchingStrategy(MatchingStrategies.STRICT)
                // 아래 두 줄은 보통 setMatchingStrategy와 함께 사용되거나, 필요에 따라 개별 설정
                .setFieldMatchingEnabled(true) // 필드 매칭 활성화
                .setSkipNullEnabled(true);    // null 값은 매핑에서 제외

        // 만약 특정 접근 레벨의 필드만 매핑하고 싶다면 아래와 같이 사용
        // (보통은 기본 전략으로 충분하며, Lombok 사용 시 Getter/Setter가 생성되므로 명시할 필요가 적음)
        // .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) // PRIVATE 필드까지 매핑 (선택 사항)
        return modelMapper;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
            //"나는 PasswordEncoder라는 역할(인터페이스)을 수행할 객체로 BCryptPasswordEncoder라는 구체적인 구현체를 사용할 것
        // Spring이 애플리케이션 시작 시점에 BCryptPasswordEncoder 객체를 생성하여
            // PasswordEncoder 타입의 빈으로 등록하고, 이 빈을 다른 서비스나 컨트롤러에서 주입받아 비밀번호 암호화 로직에 활용할 수 있도록 하기 위함
    }
}
