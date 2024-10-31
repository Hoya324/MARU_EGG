package mju.iphak.maru_egg.admission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeDetail;

public interface AdmissionTypeDetailRepository extends JpaRepository<AdmissionTypeDetail, Long> {

	@Query("SELECT a FROM AdmissionTypeDetail a WHERE a.admissionTypeStatus.admissionType = :admissionType")
	List<AdmissionTypeDetail> findAllByAdmissionType(@Param("admissionType") AdmissionType admissionType);
}
