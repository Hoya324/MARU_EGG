package mju.iphak.maru_egg.question.docs;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mju.iphak.maru_egg.question.dto.request.CheckQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.UpdateQuestionContentRequest;

@Tag(name = "Admin Question API", description = "어드민 질문 관련 API 입니다.")
public interface AdminQuestionControllerDocs {

	@Operation(summary = "질문 수정", description = "질문을 수정하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문을 수정 성공"),
	})
	void updateQuestionContent(@Valid @RequestBody UpdateQuestionContentRequest request);

	@Operation(summary = "질문-답변 체크", description = "질문-답변이 확인된 질문인지 체크하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 체크 성공")
	})
	void checkQuestion(@Valid @RequestBody CheckQuestionRequest request);

	@Operation(summary = "커스텀 질문 생성", description = "질문을 생성하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 생성 성공")
	})
	void createQuestion(@Valid @RequestBody CreateQuestionRequest request);

	@Operation(summary = "질문 삭제", description = "질문을 삭제하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 삭제 성공")
	})
	void deleteQuestion(@PathVariable("questionId") Long id);
}
