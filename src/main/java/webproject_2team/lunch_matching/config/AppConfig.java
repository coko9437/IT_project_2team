package webproject_2team.lunch_matching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 애플리케이션의 일반적인 설정을 담당하는 클래스입니다.
 * 여기서는 외부 API 호출에 사용되는 RestTemplate을 Spring Bean으로 등록합니다.
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate을 Spring Bean으로 등록합니다.
     * RestTemplate은 HTTP 요청을 보내는 데 사용됩니다.
     *
     * @return RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}