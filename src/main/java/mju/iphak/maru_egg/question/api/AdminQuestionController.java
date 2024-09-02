package mju.iphak.maru_egg.question.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.meta.CustomApiResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponses;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.docs.AdminQuestionControllerDocs;
import mju.iphak.maru_egg.question.dto.request.CheckQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController implements AdminQuestionControllerDocs {

	private final QuestionService questionService;

	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "\"Invalid input format: JSON parse error: Cannot deserialize value of type `java.lang.Long` from String \\\"잘못된 형식의 질문 ID\\\": not a valid `java.lang.Long` value\"", description = "json 형식 및 타입에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "EntityNotFoundException", status = 404, message = "id: 132인 질문을 찾을 수 없습니다.", description = "질문을 찾지 못한 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@PutMapping("/check")
	public void checkQuestion(@Valid @RequestBody CheckQuestionRequest request) {
		questionService.checkQuestion(request.questionId());
	}

	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "Invalid input format: JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionType` from String \\\"SUSI 또는 PYEONIP 또는 JEONGSI\\\": not one of the values accepted for Enum class: [SUSI, PYEONIP, JEONGSI]", description = "validation에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@PostMapping("/new")
	public void createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
		questionService.createQuestion(request);
	}

	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "Invalid input format: JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionType` from String \\\"SUSI 또는 PYEONIP 또는 JEONGSI\\\": not one of the values accepted for Enum class: [SUSI, PYEONIP, JEONGSI]", description = "validation에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "EntityNotFoundException", status = 404, message = "id: 132인 질문을 찾을 수 없습니다.", description = "질문을 찾지 못한 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@DeleteMapping("/{questionId}")
	public void deleteQuestion(@PathVariable("questionId") Long id) {
		questionService.deleteQuestion(id);
	}
}
