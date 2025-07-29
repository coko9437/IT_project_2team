package webproject_2team.lunch_matching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LunchMatchingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LunchMatchingApplication.class, args);
	}

}
