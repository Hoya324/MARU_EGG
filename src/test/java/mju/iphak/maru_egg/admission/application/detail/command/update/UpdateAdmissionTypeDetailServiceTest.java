package mju.iphak.maru_egg.admission.application.detail.command.update;

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
import mju.iphak.maru_egg.admission.repository.AdmissionTypeDetailRepository;
import mju.iphak.maru_egg.common.MockTest;

class UpdateAdmissionTypeDetailServiceTest extends MockTest {

	@Mock
	private AdmissionTypeDetailRepository admissionTypeDetailRepository;

	@InjectMocks
	private UpdateAdmissionTypeDetailService updateAdmissionTypeDetail;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("[실패] 상세 정보 업데이트 요청 - 존재하지 않는 ID")
	@Test
	void 상세정보_업데이트_실패_존재하지_않는_ID() {
		// given
		Long invalidId = 999L;
		when(admissionTypeDetailRepository.findById(invalidId)).thenReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
			() -> updateAdmissionTypeDetail.invoke(invalidId, "newName"));

		// then
		assertEquals("id가 999인 입학 전형 상세를 찾을 수 없습니다.", exception.getMessage());
	}
}
