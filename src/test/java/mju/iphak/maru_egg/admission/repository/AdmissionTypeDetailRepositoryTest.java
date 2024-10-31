package mju.iphak.maru_egg.admission.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeDetail;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.common.RepositoryTest;

class AdmissionTypeDetailRepositoryTest extends RepositoryTest {

	@Autowired
	private AdmissionTypeStatusRepository admissionTypeStatusRepository;

	@Autowired
	private AdmissionTypeDetailRepository admissionTypeDetailRepository;

	private AdmissionTypeDetail admissionTypeDetail;

	@BeforeEach
	void setUp() {
		AdmissionType type = AdmissionType.SUSI;
		AdmissionTypeStatus admissionTypeStatus = AdmissionTypeStatus.of(AdmissionType.SUSI);
		admissionTypeStatusRepository.save(admissionTypeStatus);
		admissionTypeDetail = AdmissionTypeDetail.of("학교장추천전형", admissionTypeStatus);
		admissionTypeDetailRepository.save(admissionTypeDetail);
	}

	@DisplayName("AdmissionType으로 상세정보 조회 성공")
	@Test
	void findAllByAdmissionType_Success() {
		// given
		AdmissionType type = AdmissionType.SUSI;

		// when
		List<AdmissionTypeDetail> result = admissionTypeDetailRepository.findAllByAdmissionType(type);

		// then
		assertThat(result).isNotEmpty();
	}
}
