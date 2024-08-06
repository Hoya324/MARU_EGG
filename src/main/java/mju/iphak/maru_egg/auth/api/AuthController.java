package mju.iphak.maru_egg.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.auth.application.AuthService;
import mju.iphak.maru_egg.auth.docs.AuthControllerDocs;
import mju.iphak.maru_egg.auth.dto.request.SignInRequest;
import mju.iphak.maru_egg.auth.dto.request.SignUpRequest;
import mju.iphak.maru_egg.common.meta.LoginUser;
import mju.iphak.maru_egg.user.domain.User;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

	private final AuthService authService;

	@PostMapping("/sign-up")
	public void register(@Valid @RequestBody SignUpRequest request) {
		authService.signUp(request.email(), request.password());
	}

	@PostMapping("/sign-in")
	public void login(@Valid @RequestBody SignInRequest request) {
		authService.signIn(request.email(), request.password());
	}

	@GetMapping("/me")
	public String getCurrentUser(@LoginUser User user) {
		return user.getEmail();
	}
}