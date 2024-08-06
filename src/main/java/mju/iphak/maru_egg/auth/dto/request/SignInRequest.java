package mju.iphak.maru_egg.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "로그인 요청 DTO")
public record SignInRequest(

	@Schema(description = "이메일", example = "test@gmail.com")
	@Email
	@NotEmpty
	String email,

	@Schema(description = "비밀번호", example = "Testtest124!")
	@NotEmpty
	String password
) {
}
