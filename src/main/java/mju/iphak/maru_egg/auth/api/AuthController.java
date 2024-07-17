package mju.iphak.maru_egg.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.auth.application.AuthService;
import mju.iphak.maru_egg.auth.dto.request.SignInRequest;
import mju.iphak.maru_egg.auth.dto.request.SignUpRequest;
import mju.iphak.maru_egg.question.meta.LoginUser;
import mju.iphak.maru_egg.user.domain.User;

@Tag(name = "Auth API", description = "인증 관련 API입니다.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "회원가입 요청", description = "회원가입을 하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "회원가입 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "500", description = "내부 서버 오류")
	})
	@PostMapping("/sign-up")
	public void register(@Valid @RequestBody SignUpRequest request) {
		authService.signUp(request.email(), request.password());
	}

	@Operation(summary = "로그인 요청", description = "로그인을 하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "500", description = "내부 서버 오류")
	})
	@PostMapping("/sign-in")
	public void login(@Valid @RequestBody SignInRequest request) {
		authService.signIn(request.email(), request.password());
	}

	@Operation(summary = "현재 사용자 정보", description = "현재 로그인한 사용자의 정보를 가져오는 API", responses = {
		@ApiResponse(responseCode = "200", description = "현재 사용자 정보 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "500", description = "내부 서버 오류")
	})
	@GetMapping("/me")
	public String getCurrentUser(@LoginUser User user) {
		return user.getEmail();
	}
}