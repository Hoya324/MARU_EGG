package mju.iphak.maru_egg.admission.application.status.command.delete;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;
import mju.iphak.maru_egg.common.MockTest;

class DeleteAdmissionTypeStatusServiceTest extends MockTest {

	@Mock
	private AdmissionTypeStatusRepository admissionTypeStatusRepository;

	@InjectMocks
	private DeleteAdmissionTypeStatusService deleteAdmissionTypeStatus;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("[성공] 입학 전형 상태 삭제 요청")
	@Test
	void 입학_전형_상태_삭제_성공() {
		// given
		AdmissionType type = AdmissionType.SUSI;

		// when
		deleteAdmissionTypeStatus.invoke(type);

		// then
		verify(admissionTypeStatusRepository, times(1)).deleteByAdmissionType(eq(type));
	}
}
