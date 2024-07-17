package mju.iphak.maru_egg.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청 DTO")
public record SignUpRequest(

	@Schema(description = "이메일")
	@Email(message = "이메일 형식을 지켜주세요.")
	@NotBlank(message = "email은 비어있을 수 없습니다.")
	String email,

	@Schema(description = "비밀번호")
	@Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야합니다.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$", message = "비밀번호는 문자, 숫자, 기호가 1개 이상 포함되어야합니다.")
	String password
) {
}
