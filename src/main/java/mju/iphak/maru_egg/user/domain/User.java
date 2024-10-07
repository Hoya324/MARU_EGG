package mju.iphak.maru_egg.user.domain;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mju.iphak.maru_egg.common.entity.BaseEntity;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

	private String email;

	private String password;

	private String refreshToken;

	@Enumerated(EnumType.STRING)
	private Role role;

	public void passwordEncode(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(this.password);
	}

	public void updateRefreshToken(String updateRefreshToken) {
		this.refreshToken = updateRefreshToken;
	}

	public static User of(String email, String password) {
		return User.builder()
			.email(email)
			.password(password)
			.role(Role.ADMIN)
			.build();
	}
}


