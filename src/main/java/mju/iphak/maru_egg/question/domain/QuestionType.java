package mju.iphak.maru_egg.question.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@Schema(description = "질문 타입", enumAsRef = true)
@RequiredArgsConstructor
public enum QuestionType {
	SUSI("수시"),
	JEONGSI("정시"),
	PYEONIP("편입학"),
	JAEOEGUGMIN("재외국민");

	private final String questionType;

	@Override
	public String toString() {
		return this.questionType;
	}
}
