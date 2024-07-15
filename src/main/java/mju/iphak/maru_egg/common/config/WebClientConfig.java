package mju.iphak.maru_egg.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfig {

	@Value("${web-client.base-url}")
	private String baseUrl;

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder
			.baseUrl(baseUrl)
			.filter(logRequest())
			.filter(logResponse())
			.build();
	}

	private ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
			clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
			return Mono.just(clientRequest);
		});
	}

	private ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			log.info("Response Status: {}", clientResponse.statusCode());
			clientResponse.headers()
				.asHttpHeaders()
				.forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
			return Mono.just(clientResponse);
		});
	}
}