package mju.iphak.maru_egg.auth.jwt.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.auth.CustomUserDetails;
import mju.iphak.maru_egg.auth.jwt.service.JwtService;
import mju.iphak.maru_egg.auth.jwt.util.PasswordUtil;
import mju.iphak.maru_egg.user.domain.User;
import mju.iphak.maru_egg.user.repository.UserRepository;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
	private static final String NO_CHECK_URL_LOGIN = "/api/auth/sign-in";
	private static final String NO_CHECK_URL_HOME = "/api";

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		if (request.getRequestURI().equals(NO_CHECK_URL_LOGIN) || request.getRequestURI().equals(NO_CHECK_URL_HOME)) {
			filterChain.doFilter(request, response);
			return;
		}

		String refreshToken = jwtService.extractRefreshToken(request)
			.filter(jwtService::isTokenValid)
			.orElse(null);

		if (refreshToken != null) {
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}

		checkAccessTokenAndAuthentication(request, response, filterChain);
	}

	private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		userRepository.findByRefreshToken(refreshToken)
			.ifPresent(user -> {
				String reIssuedRefreshToken = reIssueRefreshToken(user);
				jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),
					reIssuedRefreshToken);
			});
	}

	private String reIssueRefreshToken(User user) {
		String reIssuedRefreshToken = jwtService.createRefreshToken();
		user.updateRefreshToken(reIssuedRefreshToken);
		userRepository.saveAndFlush(user);
		return reIssuedRefreshToken;
	}

	private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain)
		throws ServletException, IOException {
		jwtService.extractAccessToken(request)
			.filter(jwtService::isTokenValid)
			.ifPresent(accessToken -> jwtService.extractEmail(accessToken)
				.ifPresent(email -> userRepository.findByEmail(email)
					.ifPresent(this::saveAuthentication)));

		filterChain.doFilter(request, response);
	}

	private void saveAuthentication(User myUser) {
		String password = myUser.getPassword();
		if (password == null) {
			password = PasswordUtil.generateRandomPassword();
		}

		CustomUserDetails userDetailsUser = new CustomUserDetails(myUser);

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null,
			authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
