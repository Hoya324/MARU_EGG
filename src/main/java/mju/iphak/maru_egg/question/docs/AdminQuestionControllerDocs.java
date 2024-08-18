package mju.iphak.maru_egg.question.docs;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mju.iphak.maru_egg.question.dto.request.CheckQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;

@Tag(name = "Admin Question API", description = "어드민 질문 관련 API 입니다.")
public interface AdminQuestionControllerDocs {

	@Operation(summary = "질문-답변 체크", description = "질문-답변이 확인된 질문인지 체크하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 체크 성공")
	})
	void checkQuestion(@Valid @RequestBody CheckQuestionRequest request);

	@Operation(summary = "커스텀 질문 생성", description = "질문을 생성하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 생성 성공")
	})
	void createQuestion(@Valid @RequestBody CreateQuestionRequest request);
}
