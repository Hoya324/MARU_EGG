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
	NOT_FOUND_QUESTION_BY_ID(NOT_FOUND, "id: %s인 질문을 찾을 수 없습니다."),
	NOT_FOUND_QUESTION_WITHOUT_CATEGORY(NOT_FOUND, "type: %s, content: %s인 질문을 찾을 수 없습니다."),
	NOT_FOUND_ANSWER(NOT_FOUND, "질문 id가 %s인 답변을 찾을 수 없습니다."),

	// 500 error
	INTERNAL_ERROR_SIMILARITY(NOT_FOUND, "contentToken: %s, question: %s인 질문의 유사도를 검사하는 도중 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String message;
}
