package mju.iphak.maru_egg.question.domain;

import jakarta.persistence.Entity;
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

	private String content;
	private QuestionType questionType;
	private QuestionCategory questionCategory;

	public String getDateInformation() {
		return "생성일자: %s, 마지막 DB 갱신일자: %s".formatted(this.getCreatedAt(), this.getUpdatedAt());
	}

	public static Question of(String content, QuestionType questionType, QuestionCategory questionCategory) {
		return Question.builder()
			.content(content)
			.questionType(questionType)
			.questionCategory(questionCategory)
			.build();
	}
}
