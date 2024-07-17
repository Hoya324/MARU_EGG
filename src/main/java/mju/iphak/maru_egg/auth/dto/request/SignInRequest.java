package mju.iphak.maru_egg.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record SignInRequest(

	@Email
	@NotEmpty
	String email,

	@NotEmpty
	String password
) {
}
