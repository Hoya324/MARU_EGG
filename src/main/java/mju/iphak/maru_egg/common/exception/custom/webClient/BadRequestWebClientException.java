package mju.iphak.maru_egg.common.exception.custom.webClient;

import org.springframework.web.reactive.function.client.WebClientException;

public class BadRequestWebClientException extends WebClientException {
	public BadRequestWebClientException(final String msg) {
		super(msg);
	}

	public BadRequestWebClientException(final String msg, final Throwable ex) {
		super(msg, ex);
	}
}
