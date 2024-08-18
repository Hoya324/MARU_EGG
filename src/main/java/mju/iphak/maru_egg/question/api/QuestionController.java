package mju.iphak.maru_egg.question.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponses;
import mju.iphak.maru_egg.question.application.QuestionProcessingService;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.docs.QuestionControllerDocs;
import mju.iphak.maru_egg.question.dto.request.FindQuestionsRequest;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.request.SearchQuestionsRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionListItemResponse;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class QuestionController implements QuestionControllerDocs {

	private final QuestionProcessingService questionProcessingService;
	private final QuestionService questionService;

	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "Invalid input format: JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionType` from String \\\"SUSI 또는 PYEONIP 또는 JEONGSI\\\": not one of the values accepted for Enum class: [SUSI, PYEONIP, JEONGSI]", description = "validation에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "EntityNotFoundException", status = 404, message = "type: SUSI, category: PAST_QUESTIONS, content: 수시 입학 요강에 대해 알려주세요.인 질문을 찾을 수 없습니다.", description = "질문 또는 답변을 찾지 못한 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@PostMapping("/questions")
	public QuestionResponse question(@Valid @RequestBody QuestionRequest request) {
		return questionProcessingService.question(request.type(), request.category(), request.content());
	}

	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "Invalid input format: JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionType` from String \\\"SUSI 또는 PYEONIP 또는 JEONGSI\\\": not one of the values accepted for Enum class: [SUSI, PYEONIP, JEONGSI]", description = "validation에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@GetMapping("/questions")
	public List<QuestionListItemResponse> getQuestions(@Valid @ModelAttribute FindQuestionsRequest request) {
		return questionService.getQuestions(request.type(), request.category());
	}

	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "Invalid input format: JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionType` from String \\\"SUSI 또는 PYEONIP 또는 JEONGSI\\\": not one of the values accepted for Enum class: [SUSI, PYEONIP, JEONGSI]", description = "validation에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@GetMapping("/questions/search")
	public SliceQuestionResponse<SearchedQuestionsResponse> searchQuestions(
		@Valid @ModelAttribute SearchQuestionsRequest request) {
		return questionService.searchQuestionsOfCursorPaging(request.content(), request.cursorViewCount(),
			request.questionId(), request.size());
	}

	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "Invalid input format: JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionType` from String \\\"SUSI 또는 PYEONIP 또는 JEONGSI\\\": not one of the values accepted for Enum class: [SUSI, PYEONIP, JEONGSI]", description = "validation에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "EntityNotFoundException", status = 404, message = "type: SUSI, category: PAST_QUESTIONS, content: 수시 입학 요강에 대해 알려주세요.인 질문을 찾을 수 없습니다.", description = "질문 또는 답변을 찾지 못한 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@GetMapping("/question")
	public QuestionResponse getQuestion(
		@Parameter(description = "조회할 질문 아이디", example = "1") @RequestParam("questionId") Long questionId) {
		return questionProcessingService.getQuestion(questionId);
	}
}