package mju.iphak.maru_egg.auth;

import lombok.Getter;
import mju.iphak.maru_egg.user.domain.User;

@Getter
public class UserAdapter extends CustomUserDetails {

	private final User user;

	public UserAdapter(final User user) {
		super(user);
		this.user = user;
	}
}
