package mju.iphak.maru_egg.admission.api.swagger;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mju.iphak.maru_egg.admission.dto.request.UpdateAdmissionTypeStatusRequest;
import mju.iphak.maru_egg.common.meta.CustomApiResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponses;

@Tag(name = "Admin QuestionTypeStatus API", description = "어드민 질문타입 상태 관련 API 입니다.")
public interface AdminAdmissionTypeStatusControllerDocs {

	@Operation(summary = "질문타입 초기화", description = "현재 제공 중인 질문 타입에 대해 DB에 초기화합니다.", responses = {
		@ApiResponse(responseCode = "200", description = "질문타입 초기화 성공")
	})
	@CustomApiResponses({
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	void initializeQuestionTypeStatus();

	@Operation(summary = "질문타입 상태 변경", description = "질문타입의 상태를 변경합니다. (true <-> false)", responses = {
		@ApiResponse(responseCode = "200", description = "질문타입 상태 변경 성공")
	})
	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "\"Invalid input format: JSON parse error: Cannot deserialize value of type `java.lang.Long` from String \\\"잘못된 형식의 질문 ID\\\": not a valid `java.lang.Long` value\"", description = "json 형식 및 타입에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "EntityNotFoundException", status = 404, message = "질문 타입이 수시인 질문 상태를 찾을 수 없습니다", description = "해당 타입을 찾을 수 없는 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	void updateQuestionTypeStatus(@Valid @RequestBody UpdateAdmissionTypeStatusRequest request);
}
