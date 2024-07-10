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
	NOT_FOUND_QUESTION(NOT_FOUND, "type: %s, category: %s, content: %s인 질문을 찾을 수 없습니다."),
	NOT_FOUND_ANSWER(NOT_FOUND, "질문 id가 %s인 답변을 찾을 수 없습니다.");

	// 500 error

	private final HttpStatus status;
	private final String message;
}
