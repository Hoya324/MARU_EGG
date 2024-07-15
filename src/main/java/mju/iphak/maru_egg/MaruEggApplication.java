package mju.iphak.maru_egg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MaruEggApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaruEggApplication.class, args);
	}

}
