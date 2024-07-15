package mju.iphak.maru_egg.question.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mju.iphak.maru_egg.common.entity.BaseEntity;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	private String contentToken;

	@Enumerated(EnumType.STRING)
	private QuestionType questionType;

	@Enumerated(EnumType.STRING)
	private QuestionCategory questionCategory;

	@Column(name = "view_count", nullable = false)
	private int viewCount;

	public String getDateInformation() {
		return "생성일자: %s, 마지막 DB 갱신일자: %s".formatted(this.getCreatedAt(), this.getUpdatedAt());
	}

	public static Question of(String content, QuestionType questionType, QuestionCategory questionCategory) {
		return Question.builder()
			.content(content)
			.contentToken(contentToken)
			.questionType(questionType)
			.questionCategory(questionCategory)
			.build();
	}
}
