package mju.iphak.maru_egg.question.domain;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mju.iphak.maru_egg.answer.domain.Answer;
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

	@ColumnDefault("false")
	private boolean isChecked;

	@OneToOne(mappedBy = "question", orphanRemoval = true)
	private Answer answer;

	public String getDateInformation() {
		return "생성일자: %s, 마지막 DB 갱신일자: %s".formatted(this.getCreatedAt(), this.getUpdatedAt());
	}

	public void incrementViewCount() {
		this.viewCount++;
	}

	public void updateIsChecked() {
		this.isChecked = !this.isChecked;
	}

	public void updateContent(String content) {
		this.content = content != null ? content : this.content;
	}

	public static Question of(String content, String contentToken, QuestionType questionType,
		QuestionCategory questionCategory) {
		return Question.builder()
			.content(content)
			.contentToken(contentToken)
			.questionType(questionType)
			.questionCategory(questionCategory)
			.isChecked(false)
			.build();
	}

	public static Question create(String content, String contentToken, QuestionType questionType,
		QuestionCategory questionCategory) {
		return Question.builder()
			.content(content)
			.contentToken(contentToken)
			.questionType(questionType)
			.questionCategory(questionCategory)
			.isChecked(true)
			.build();
	}
}
