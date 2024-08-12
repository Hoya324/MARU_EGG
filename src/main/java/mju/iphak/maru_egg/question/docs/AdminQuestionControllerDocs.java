package mju.iphak.maru_egg.question.docs;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mju.iphak.maru_egg.common.meta.CustomApiResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponses;
import mju.iphak.maru_egg.question.dto.request.CheckQuestionRequest;

@Tag(name = "Admin Question API", description = "어드민 질문 관련 API 입니다.")
public interface AdminQuestionControllerDocs {

	@Operation(summary = "질문-답변 체크", description = "질문-답변이 확인된 질문인지 체크하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 체크 성공")
	})
	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "\"Invalid input format: JSON parse error: Cannot deserialize value of type `java.lang.Long` from String \\\"잘못된 형식의 질문 ID\\\": not a valid `java.lang.Long` value\"", description = "json 형식 및 타입에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "EntityNotFoundException", status = 404, message = "id: 132인 질문을 찾을 수 없습니다.", description = "질문을 찾지 못한 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	void checkQuestion(@Valid @RequestBody CheckQuestionRequest request);

	@Operation(summary = "질문, 답변 삭제", description = "질문과 답변을 삭제하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문과 답변 삭제 성공")
	})
	@CustomApiResponses({
		@CustomApiResponse(error = "EntityNotFoundException", status = 404, message = "id: 132인 질문을 찾을 수 없습니다.", description = "질문을 찾지 못한 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	void deleteQuestion(@RequestParam Long questionId);
}
