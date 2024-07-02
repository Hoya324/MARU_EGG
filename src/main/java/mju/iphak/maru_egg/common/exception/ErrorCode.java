package mju.iphak.maru_egg.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 400 error

	// 404 error
	NOT_FOUND_CHAT(NOT_FOUND, "id: %s인 채팅방을 찾을 수 없습니다."),
	NOT_FOUND_CHAT_MESSAGE_IN_CHAT(NOT_FOUND, "id: %s인 채팅방의 채팅을 찾을 수 없습니다.");

	// 500 error

	private final HttpStatus status;
	private final String message;
}
