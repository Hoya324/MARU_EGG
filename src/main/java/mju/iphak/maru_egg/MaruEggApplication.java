package mju.iphak.maru_egg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
	servers = {
		@Server(url = "https://marueggserver.com", description = "Default Server url")
	}
)
@SpringBootApplication
@EnableJpaAuditing
public class MaruEggApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaruEggApplication.class, args);
	}

}
