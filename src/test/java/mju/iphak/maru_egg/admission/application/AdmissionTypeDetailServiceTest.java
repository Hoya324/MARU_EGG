package mju.iphak.maru_egg.admission.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeDetail;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.dto.response.AdmissionTypeDetailResponse;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeDetailRepository;
import mju.iphak.maru_egg.common.MockTest;

class AdmissionTypeDetailServiceTest extends MockTest {

	@Mock
	private AdmissionTypeDetailRepository admissionTypeDetailRepository;

	@InjectMocks
	private AdmissionTypeDetailService admissionTypeDetailService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("상세 정보 조회 성공")
	@Test
	void findAllByAdmissionType_Success() {
		// given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionTypeStatus admissionTypeStatus = AdmissionTypeStatus.of(AdmissionType.SUSI);
		AdmissionTypeDetail admissionTypeDetail = AdmissionTypeDetail.of("학교장추천전형", admissionTypeStatus);
		when(admissionTypeDetailRepository.findAllByAdmissionType(type))
			.thenReturn(List.of(admissionTypeDetail));

		// when
		List<AdmissionTypeDetailResponse> result = admissionTypeDetailService.findAllByAdmissionType(type);

		// then
		assertThat(result).isNotEmpty();
	}

	@DisplayName("상세 정보 업데이트 실패 - 존재하지 않는 ID")
	@Test
	void update_Failure_NotFound() {
		// given
		Long invalidId = 999L;
		when(admissionTypeDetailRepository.findById(invalidId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(EntityNotFoundException.class,
			() -> admissionTypeDetailService.update(invalidId, "newName"));
	}
}
