package mju.iphak.maru_egg.user.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import mju.iphak.maru_egg.common.RepositoryTest;
import mju.iphak.maru_egg.user.domain.Role;
import mju.iphak.maru_egg.user.domain.User;

public class UserRepositoryTest extends RepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private User saveUser;
	private String email;

	@BeforeEach
	public void setUp() throws Exception {
		email = "maru@gmail.com";
		saveUser = saveUser(email);
	}

	@DisplayName("[성공] 이메일이 존재하는 경우 true를 반환")
	@Test
	void 이메일_존재_true() {
		// when
		boolean existsByEmail = checkIfEmailExists(email);

		// then
		assertThat(existsByEmail).isTrue();
	}

	@DisplayName("[실패] 이메일이 존재하지 않는 경우 false를 반환")
	@Test
	void 이메일_존재하지않음_false() {
		// when
		boolean existsByEmail = checkIfEmailExists("invalid@asd.com");

		// then
		assertThat(existsByEmail).isFalse();
	}

	private User saveUser(String email) {
		return userRepository.save(User.builder().email(email).role(Role.GUEST).build());
	}

	private boolean checkIfEmailExists(String email) {
		return userRepository.findByEmail(email).isPresent();
	}
}
