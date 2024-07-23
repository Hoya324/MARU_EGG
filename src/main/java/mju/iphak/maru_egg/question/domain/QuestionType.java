package mju.iphak.maru_egg.question.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "질문 타입", enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum QuestionType {
	SUSI("수시"),
	JEONGSI("정시"),
	PYEONIP("편입학");

	private final String type;

	@Override
	public String toString() {
		return this.type;
	}
}
