package mju.iphak.maru_egg.question.domain;

import java.util.Arrays;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

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
@Table(name = "question_type_status")
public class QuestionTypeStatus extends BaseEntity {

	@Enumerated(EnumType.STRING)
	private QuestionType questionType;

	@ColumnDefault("true")
	private boolean isActivated;

	public void updateStatus() {
		this.isActivated = !this.isActivated;
	}

	public static List<QuestionTypeStatus> initialize() {
		QuestionType[] types = QuestionType.values();
		return Arrays.stream(types)
			.map(QuestionTypeStatus::of)
			.toList();
	}

	public static QuestionTypeStatus of(QuestionType type) {
		return QuestionTypeStatus.builder()
			.questionType(type)
			.isActivated(true)
			.build();
	}
}
