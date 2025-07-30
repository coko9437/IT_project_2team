package webproject_2team.lunch_matching.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Querydsl 사용에 필요한 JPAQueryFactory를 Spring의 Bean으로 등록하는 설정 클래스입니다.
 */
@Configuration
public class QuerydslConfig {

    @PersistenceContext // JPA의 EntityManager를 주입받습니다.
    private EntityManager entityManager;

    @Bean // 이 메서드가 반환하는 객체를 Spring의 Bean으로 등록합니다.
    public JPAQueryFactory jpaQueryFactory() {
        // EntityManager를 사용하여 JPAQueryFactory를 생성하고 반환합니다.
        return new JPAQueryFactory(entityManager);
    }
}