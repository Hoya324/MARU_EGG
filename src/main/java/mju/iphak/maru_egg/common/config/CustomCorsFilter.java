package mju.iphak.maru_egg.common.config;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter implements Filter {

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws
		IOException,
		ServletException {
		HttpServletResponse response = (HttpServletResponse)res;
		HttpServletRequest request = (HttpServletRequest)req;
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000, https://maru-egg-fe.vercel.app/");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
			"Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");
		response.setHeader("Access-Control-Expose-Headers", "*, Authorization, Authorization-refresh");

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, res);
		}
	}
}
