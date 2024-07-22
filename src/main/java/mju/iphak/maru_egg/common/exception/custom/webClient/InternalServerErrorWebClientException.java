package mju.iphak.maru_egg.common.exception.custom.webClient;

import org.springframework.web.reactive.function.client.WebClientException;

public class InternalServerErrorWebClientException extends WebClientException {
	public InternalServerErrorWebClientException(final String msg) {
		super(msg);
	}

	public InternalServerErrorWebClientException(final String msg, final Throwable ex) {
		super(msg, ex);
	}
}
