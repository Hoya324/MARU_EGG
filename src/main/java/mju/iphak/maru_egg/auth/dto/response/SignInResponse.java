package mju.iphak.maru_egg.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "로그인 응답 DTO")
@Builder
public record SignInResponse(
	@Schema(description = "acessToken")
	String accessToken
) {

	public static SignInResponse from(String accessToken) {
		return SignInResponse.builder()
			.accessToken(accessToken)
			.build();
	}
}
