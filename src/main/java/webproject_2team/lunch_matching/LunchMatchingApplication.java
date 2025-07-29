package webproject_2team.lunch_matching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // 부모 베이스 엔티티 클래스에 설정된 리스너가 동작을함.
public class LunchMatchingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LunchMatchingApplication.class, args);
	}

}
