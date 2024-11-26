package mju.iphak.maru_egg.auth.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import mju.iphak.maru_egg.auth.application.AuthService;
import mju.iphak.maru_egg.auth.dto.request.SignUpRequest;
import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.user.domain.User;
import mju.iphak.maru_egg.user.repository.UserRepository;

class AuthControllerTest extends IntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	@SpyBean
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private User user;

	@BeforeEach
	void setUp() {
		user = createTestUser("testuser@example.com", "Password123!");
		userRepository.save(user);
	}

	@DisplayName("[성공] 회원가입 요청")
	@Test
	void 회원가입_성공() throws Exception {
		// given
		SignUpRequest signUpRequest = new SignUpRequest("testuser2@example.com", "Password123!");

		// when
		ResultActions resultActions = SignUp(signUpRequest);

		// then
		resultActions.andExpect(status().isOk());
		verify(authService, times(1)).signUp(signUpRequest.email(), signUpRequest.password());
	}

	private ResultActions SignUp(SignUpRequest request) throws Exception {
		return mvc.perform(post("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());
	}

	private User createTestUser(String email, String rawPassword) {
		return User.of(email, passwordEncoder.encode(rawPassword));
	}
}
