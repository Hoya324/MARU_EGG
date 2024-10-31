package mju.iphak.maru_egg.admission.api.swagger;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mju.iphak.maru_egg.admission.dto.response.AdmissionTypeStatusResponse;

@Tag(name = "QuestionTypeStatus API", description = "질문타입 상태 관련 API 입니다.")
public interface AdmissionTypeStatusControllerDocs {

	@Operation(summary = "전체 질문타입과 상태 조회", description = "전체 질문타입과 상태를 조회합니다.", responses = {
		@ApiResponse(responseCode = "200", description = "전체 질문타입과 상태 조회 성공")
	})
	List<AdmissionTypeStatusResponse> getQuestionTypeStatus();
}
