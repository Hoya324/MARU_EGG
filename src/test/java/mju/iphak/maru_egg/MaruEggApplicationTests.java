package mju.iphak.maru_egg;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import mju.iphak.maru_egg.common.config.QueryDslConfig;
import mju.iphak.maru_egg.common.config.SecurityConfig;
import mju.iphak.maru_egg.common.config.WebClientConfig;

@Import({TestcontainersConfiguration.class, QueryDslConfig.class, WebClientConfig.class, SecurityConfig.class})
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
	"web-client.base-url=http://localhost:8080",
	"JWT_SECRET=my_test_secret_key"
})
class MaruEggApplicationTests {

	@Test
	void contextLoads() {
	}
}
