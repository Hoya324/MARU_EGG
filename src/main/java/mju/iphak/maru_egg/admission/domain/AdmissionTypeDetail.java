package mju.iphak.maru_egg.admission.domain;

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
@Table(name = "question_type_detail")
public class AdmissionTypeDetail extends BaseEntity {

	@Enumerated(EnumType.STRING)
	private AdmissionType admissionType;

	private String questionTypeDetail;

	public void updateDetail(String questionTypeDetail) {
		this.questionTypeDetail = questionTypeDetail;
	}
}
