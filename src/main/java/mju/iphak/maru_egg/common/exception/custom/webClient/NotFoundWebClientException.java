package mju.iphak.maru_egg.common.exception.custom.webClient;

import org.springframework.web.reactive.function.client.WebClientException;

public class NotFoundWebClientException extends WebClientException {
	public NotFoundWebClientException(final String msg) {
		super(msg);
	}

	public NotFoundWebClientException(final String msg, final Throwable ex) {
		super(msg, ex);
	}
}
