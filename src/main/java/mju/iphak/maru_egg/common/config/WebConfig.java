package mju.iphak.maru_egg.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final String ORIGIN_LOCAL_URL = "http://localhost:3000";
	private static final String ORIGIN_URL = "https://maru-egg-fe.vercel.app";
	private static final String ORIGIN_SERVER_HTTP_DOMAIN_URL = "http://marueggserver.com";
	private static final String ORIGIN_SERVER_HTTPS_DOMAIN_URL = "https://marueggserver.com";

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(ORIGIN_LOCAL_URL, ORIGIN_URL, ORIGIN_SERVER_HTTP_DOMAIN_URL, ORIGIN_SERVER_HTTPS_DOMAIN_URL)
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedHeaders("Authorization", "Content-Type")
			.exposedHeaders("Authorization")
			.exposedHeaders("Authorization-refresh")
			.allowCredentials(true)
			.maxAge(3600);
	}
}
