package webproject_2team.lunch_matching.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {

    // WebSecurityCustomizer를 사용하여 특정 요청을 보안 필터 체인에서 완전히 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("=====WebSecurityCustomizer=====");
        return (web) -> web.ignoring()
                // 정적 리소스 경로를 보안 필터 체인에서 완전히 무시
                .requestMatchers(
                        "/", // 루트 경로도 포함 (혹시 모를 경우)
                        "/signup.html", // 회원가입 HTML 파일 자체
                        "/static/js/**",       // 모든 JavaScript 파일
                        "/css/**",      // 모든 CSS 파일
                        "/images/**",   // 모든 이미지 파일
                        "/favicon.ico", // 파비콘
                        "/error/**",     // 에러 페이지도 무시 (선택 사항)
                        "/.well-known/**" // 크롬 개발자 도구 관련 경로 무시
                )
                // PathRequest.toStaticResources().atCommonLocations()도 함께 사용 가능 (더 많은 기본 경로 포함)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("=====securityFilterChain=====");

        // CSRF 비활성화 (REST API 개발중)
        http.csrf(AbstractHttpConfigurer::disable);
        // 특정 경로에 대한 접근 권한 설정
        http.authorizeHttpRequests(authorize -> authorize
                // 회원가입, 이메일 인증 API는 인증 없이 접근 허용
                .requestMatchers("/api/members/**", "/api/auth/**").permitAll()

                // H2 Console, Swagger UI, 업로드 파일 접근은 인증 없이 허용
                .requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**", "/upload/**").permitAll()

                // 그 외 모든 요청은 인증 필요 (로그인 필요)
                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // 로그인 페이지 URL 설정 (실제 로그인 페이지 경로로 변경 필요)
                        .permitAll() // 로그인 페이지 인증 없이 접근 허용
                )
                .logout(logout -> logout
                        .permitAll() // 로그아웃 인증 없이 접근 허용
        );
        // H2 Console 프레임 허용 (개발 환경에서만 필요)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // TODO: 추후 로그인, 로그아웃, 예외 처리 등을 추가

        return http.build();
    }
}