package mju.iphak.maru_egg.common;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(properties = {
	"web-client.base-url-test=http://localhost:8080",
	"jwt.secretKey-test=my_test_secret_key"
})
@Ignore
public class MockTest {
}
