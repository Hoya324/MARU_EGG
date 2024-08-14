package mju.iphak.maru_egg.answer.docs;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mju.iphak.maru_egg.answer.dto.request.UpdateAnswerContentRequest;

@Tag(name = "Admin Answer API", description = "어드민 답변 관련 API 입니다.")
public interface AdminAnswerControllerDocs {

	@Operation(summary = "답변 수정", description = "답변을 수정하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "답변을 수정 성공"),
	})
	void updateAnswerContent(@Valid @RequestBody UpdateAnswerContentRequest request);
}
