package mju.iphak.maru_egg.admission.domain;

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
import mju.iphak.maru_egg.common.entity.BaseEntity;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "admission_type_detail")
public class AdmissionTypeDetail extends BaseEntity {

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admission_type_status_id")
	private AdmissionTypeStatus admissionTypeStatus;

	public void updateDetailName(String name) {
		this.name = name;
	}

	public static AdmissionTypeDetail of(String name, AdmissionTypeStatus admissionTypeStatus) {
		return AdmissionTypeDetail.builder()
			.name(name)
			.admissionTypeStatus(admissionTypeStatus)
			.build();
	}
}
