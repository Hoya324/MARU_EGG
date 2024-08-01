package mju.iphak.maru_egg.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.user.domain.User;
import mju.iphak.maru_egg.user.repository.UserRepository;

class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("회원 가입에 성공한 경우")
	@Test
	void 회원_가입_성공() {
		// given
		String email = "test@example.com";
		String password = "password";

		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		authService.signUp(email, password);

		// then
		verify(userRepository, times(1)).save(any(User.class));
	}

	@DisplayName("이미 존재하는 이메일로 회원 가입 시도")
	@Test
	void 이미_존재하는_이메일로_회원_가입() {
		// given
		String email = "test@example.com";
		String password = "password";
		User existingUser = mock(User.class);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

		// when & then
		assertThatThrownBy(() -> authService.signUp(email, password))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("이미 존재하는 이메일입니다.");
	}

	@DisplayName("로그인에 성공한 경우")
	@Test
	void 로그인_성공() {
		// given
		String email = "test@example.com";
		String password = "password";
		String encodedPassword = "encodedPassword";
		User user = mock(User.class);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
		when(user.getPassword()).thenReturn(encodedPassword);

		// when
		authService.signIn(email, password);

		// then
		verify(passwordEncoder, times(1)).matches(password, encodedPassword);
		verify(userRepository, times(1)).findByEmail(email); // 추가적인 검증
	}

	@DisplayName("존재하지 않는 이메일로 로그인 시도")
	@Test
	void 존재하지_않는_이메일로_로그인() {
		// given
		String email = "nonexistent@example.com";
		String password = "password";

		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> authService.signIn(email, password))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("유저 이메일이 nonexistent@example.com인 유저를 찾을 수 없습니다.");
	}

	@DisplayName("잘못된 비밀번호로 로그인 시도")
	@Test
	void 잘못된_비밀번호로_로그인() {
		// given
		String email = "test@example.com";
		String password = "wrongPassword";
		String correctPassword = "correctPassword";
		User user = mock(User.class);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(user.getPassword()).thenReturn(correctPassword);
		when(passwordEncoder.matches(password, correctPassword)).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> authService.signIn(email, password))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("로그인에 실패했습니다.");
	}
}