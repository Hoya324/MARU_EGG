package mju.iphak.maru_egg.common;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import mju.iphak.maru_egg.TestcontainersConfiguration;
import mju.iphak.maru_egg.common.config.QueryDslConfig;
import mju.iphak.maru_egg.common.config.SecurityConfig;
import mju.iphak.maru_egg.common.config.WebClientConfig;

@Import({TestcontainersConfiguration.class, QueryDslConfig.class, WebClientConfig.class, SecurityConfig.class})
@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class IntegrationTest {
	@Autowired
	protected MockMvc mvc;
	@Autowired
	protected ObjectMapper objectMapper;
}