package mju.iphak.maru_egg.answerreference.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "answer_references")
public class AnswerReference extends BaseEntity {

	private String title;

	@Column(length = 1000)
	private String link;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "answer_id")
	private Answer answer;

	public void updateAnswer(Answer answer) {
		this.answer = answer;
		answer.getReferences().add(this);
	}

	public static AnswerReference of(String title, String link, Answer answer) {
		return AnswerReference.builder()
			.title(title)
			.link(link)
			.answer(answer)
			.build();
	}
}
