package webproject_2team.lunch_matching.config;

import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration  // 현재 주석 처리된 상태로 유지합니다. 필요시 활성화하세요.
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // favicon.ico 요청을 /dev/null과 유사하게 처리하여 무시합니다.
        // 이렇게 하면 콘솔에 favicon.ico 관련 오류가 나타나지 않습니다.
        registry.addViewController("/favicon.ico").setViewName("forward:/noIcon");
    }
}