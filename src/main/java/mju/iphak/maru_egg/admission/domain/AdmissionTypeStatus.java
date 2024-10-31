package mju.iphak.maru_egg.admission.domain;

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
@Table(name = "admission_type_status")
public class AdmissionTypeStatus extends BaseEntity {

	@Enumerated(EnumType.STRING)
	private AdmissionType admissionType;

	@ColumnDefault("true")
	private boolean isActivated;

	public void updateStatus() {
		this.isActivated = !this.isActivated;
	}

	public static List<AdmissionTypeStatus> initialize() {
		AdmissionType[] types = AdmissionType.values();
		return Arrays.stream(types)
			.map(AdmissionTypeStatus::of)
			.toList();
	}

	public static AdmissionTypeStatus of(AdmissionType type) {
		return AdmissionTypeStatus.builder()
			.admissionType(type)
			.isActivated(true)
			.build();
	}
}
