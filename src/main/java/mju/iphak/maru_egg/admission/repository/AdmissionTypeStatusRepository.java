package mju.iphak.maru_egg.admission.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;

public interface AdmissionTypeStatusRepository extends JpaRepository<AdmissionTypeStatus, Long> {
	Optional<AdmissionTypeStatus> findByAdmissionType(AdmissionType type);

	void deleteByAdmissionType(final AdmissionType admissionType);
}
