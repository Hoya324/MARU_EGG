package mju.iphak.maru_egg.admission.application.status.command.update;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;
import mju.iphak.maru_egg.common.MockTest;

class UpdateAdmissionTypeStatusServiceTest extends MockTest {

	@Mock
	private AdmissionTypeStatusRepository admissionTypeStatusRepository;

	@InjectMocks
	private UpdateAdmissionTypeStatusService updateAdmissionTypeStatus;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("[성공] 입학 전형 상태 업데이트 요청")
	@Test
	void 입학_전형_상태_업데이트_성공() {
		// given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionTypeStatus status = new AdmissionTypeStatus(type, true);
		when(admissionTypeStatusRepository.findByAdmissionType(type)).thenReturn(Optional.of(status));

		// when
		updateAdmissionTypeStatus.invoke(type);

		// then
		verify(admissionTypeStatusRepository, times(1)).findByAdmissionType(type);
		assertThat(status.isActivated()).isFalse();
	}

	@DisplayName("[실패] 입학 전형 상태 업데이트 요청 - 상태 없음")
	@Test
	void 입학_전형_상태_업데이트_실패_상태_없음() {
		// given
		AdmissionType type = AdmissionType.SUSI;
		when(admissionTypeStatusRepository.findByAdmissionType(type)).thenReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
			() -> updateAdmissionTypeStatus.invoke(type));

		// then
		assertThat(exception.getMessage())
			.isEqualTo(String.format(NOT_FOUND_ADMISSION_TYPE_STATUS.getMessage(), type));
	}
}
