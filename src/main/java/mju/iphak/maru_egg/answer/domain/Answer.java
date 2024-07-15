package mju.iphak.maru_egg.answer.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mju.iphak.maru_egg.common.entity.BaseEntity;
import mju.iphak.maru_egg.question.domain.Question;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "answers")
public class Answer extends BaseEntity {

	@Column(nullable = false, length = 4000)
	private String content;

	@Column(name = "renewal_year")
	private int renewalYear;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", nullable = false)
	private Question question;

	public String getDateInformation() {
		return "생성일자: %s, 마지막 DB 갱신일자: %s".formatted(this.getCreatedAt(), this.getUpdatedAt());
	}

	public static Answer of(Question question, String content) {
		return Answer.builder()
			.content(content)
			.renewalYear(getNowYear())
			.question(question)
			.build();
	}

	@Override
	public String toString() {
		return "Answer{" +
			"content='" + content + '\'' +
			", renewalYear=" + renewalYear +
			", question=" + question +
			'}';
	}

	private static int getNowYear() {
		LocalDate nowDate = LocalDate.now();
		return Integer.parseInt(String.valueOf(nowDate.getYear()));
	}

}
