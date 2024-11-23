package mju.iphak.maru_egg.admission.application.status.query.find;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.dto.response.AdmissionTypeStatusResponse;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;
import mju.iphak.maru_egg.common.MockTest;

class FindAllAdmissionTypeStatusServiceTest extends MockTest {

	@Mock
	private AdmissionTypeStatusRepository admissionTypeStatusRepository;

	@InjectMocks
	private FindAllAdmissionTypeStatusService findAllAdmissionTypeStatus;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("입학 전형 상태 목록 조회 성공")
	@Test
	void getAdmissionTypeStatus_Success() {
		// given
		AdmissionTypeStatus status1 = new AdmissionTypeStatus(AdmissionType.SUSI, true);
		AdmissionTypeStatus status2 = new AdmissionTypeStatus(AdmissionType.JEONGSI, false);
		when(admissionTypeStatusRepository.findAll()).thenReturn(List.of(status1, status2));

		// when
		List<AdmissionTypeStatusResponse> result = findAllAdmissionTypeStatus.invoke();

		// then
		verify(admissionTypeStatusRepository, times(1)).findAll();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).type()).isEqualTo(AdmissionType.SUSI);
		assertThat(result.get(0).isActivated()).isTrue();
		assertThat(result.get(1).type()).isEqualTo(AdmissionType.JEONGSI);
		assertThat(result.get(1).isActivated()).isFalse();
	}

}