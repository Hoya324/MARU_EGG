package mju.iphak.maru_egg.common.exception;

import static org.springframework.http.HttpStatus.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.common.exception.custom.webClient.BadRequestWebClientException;
import mju.iphak.maru_egg.common.exception.custom.webClient.InternalServerErrorWebClientException;
import mju.iphak.maru_egg.common.exception.custom.webClient.NotFoundWebClientException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final String LOG_FORMAT = "Timestamp: {}, Class: {}, ErrorMessage: {}";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
		"yyyy-MM-dd'T'HH:mm:ss.SSS");

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
		String timestamp = getCurrentTimestamp();
		log.error(LOG_FORMAT, timestamp, e.getClass().getSimpleName(), e.getMessage());
		return ResponseEntity.status(INTERNAL_SERVER_ERROR)
			.body(ErrorResponse.of(e.getClass().getSimpleName(), INTERNAL_SERVER_ERROR.value(), e.getMessage()));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
		String timestamp = getCurrentTimestamp();
		log.error(LOG_FORMAT, timestamp, e.getClass().getSimpleName(), e.getMessage());
		return ResponseEntity.status(NOT_FOUND)
			.body(ErrorResponse.of(e.getClass().getSimpleName(), NOT_FOUND.value(), e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		String timestamp = getCurrentTimestamp();
		log.error(LOG_FORMAT, timestamp, e.getClass().getSimpleName(), e.getMessage());
		return ResponseEntity.status(BAD_REQUEST)
			.body(ErrorResponse.of(e.getClass().getSimpleName(), BAD_REQUEST.value(), e.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		String timestamp = getCurrentTimestamp();
		log.error(LOG_FORMAT, timestamp, e.getClass().getSimpleName(), e.getMessage());
		String errorMessage = "Invalid input format: " + e.getLocalizedMessage();
		return ResponseEntity.status(BAD_REQUEST)
			.body(ErrorResponse.of(e.getClass().getSimpleName(), BAD_REQUEST.value(), errorMessage));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	protected ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
		String timestamp = getCurrentTimestamp();
		log.error(LOG_FORMAT, timestamp, e.getClass().getSimpleName(), e.getMessage());
		return ResponseEntity.status(NOT_FOUND)
			.body(ErrorResponse.of(e.getClass().getSimpleName(), NOT_FOUND.value(), e.getMessage()));
	}

	@ExceptionHandler(BadRequestWebClientException.class)
	protected ResponseEntity<ErrorResponse> handleBadRequestWebClientException(BadRequestWebClientException e) {
		String timestamp = getCurrentTimestamp();
		log.error(LOG_FORMAT, timestamp, e.getClass().getSimpleName(), e.getMessage());
		return ResponseEntity.status(BAD_REQUEST)
			.body(ErrorResponse.of(e.getClass().getSimpleName(), BAD_REQUEST.value(), e.getMessage()));
	}

	@ExceptionHandler(NotFoundWebClientException.class)
	protected ResponseEntity<ErrorResponse> handleNotFoundWebClientException(NotFoundWebClientException e) {
		String timestamp = getCurrentTimestamp();
		log.error(LOG_FORMAT, timestamp, e.getClass().getSimpleName(), e.getMessage());
		return ResponseEntity.status(NOT_FOUND)
			.body(ErrorResponse.of(e.getClass().getSimpleName(), NOT_FOUND.value(), e.getMessage()));
	}

	@ExceptionHandler(InternalServerErrorWebClientException.class)
	protected ResponseEntity<ErrorResponse> handleInternalServerErrorWebClientException(
		InternalServerErrorWebClientException e) {
		String timestamp = getCurrentTimestamp();
		log.error(LOG_FORMAT, timestamp, e.getClass().getSimpleName(), e.getMessage());
		return ResponseEntity.status(INTERNAL_SERVER_ERROR)
			.body(ErrorResponse.of(e.getClass().getSimpleName(), INTERNAL_SERVER_ERROR.value(), e.getMessage()));
	}

	@ExceptionHandler(JpaSystemException.class)
	protected ResponseEntity<ErrorResponse> handleJpaSystemException(JpaSystemException e) {
		String timestamp = getCurrentTimestamp();
		log.error(LOG_FORMAT, timestamp, e.getClass().getSimpleName(), e.getMessage());
		return ResponseEntity.status(INTERNAL_SERVER_ERROR)
			.body(ErrorResponse.of(e.getClass().getSimpleName(), INTERNAL_SERVER_ERROR.value(), e.getMessage()));
	}

	private String getCurrentTimestamp() {
		return LocalDateTime.now().format(DATE_TIME_FORMATTER);
	}
}
