package mju.iphak.maru_egg.question.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponses;
import mju.iphak.maru_egg.question.application.QuestionProcessingService;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.dto.request.FindQuestionsRequest;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.request.SearchQuestionsRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

@Tag(name = "Question API", description = "질문 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

	private final QuestionProcessingService questionProcessingService;
	private final QuestionService questionService;

	@Operation(summary = "질문 요청", description = "질문하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 성공")
	})
	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "Invalid input format: JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionType` from String \\\"SUSI 또는 PYEONIP 또는 JEONGSI 또는 JAEOEGUGMIN\\\": not one of the values accepted for Enum class: [SUSI, PYEONIP, JEONGSI, JAEOEGUGMIN]", description = "validation에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "EntityNotFoundException", status = 404, message = "type: SUSI, category: PAST_QUESTIONS, content: 수시 입학 요강에 대해 알려주세요.인 질문을 찾을 수 없습니다.", description = "질문 또는 답변을 찾지 못한 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@PostMapping()
	public QuestionResponse question(@Valid @RequestBody QuestionRequest request) {
		return questionProcessingService.question(request.type(), request.category(), request.content());
	}

	@Operation(summary = "질문 목록 요청", description = "질문 목록을 보내주는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 성공")
	})
	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "Invalid input format: JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionType` from String \\\"SUSI 또는 PYEONIP 또는 JEONGSI 또는 JAEOEGUGMIN\\\": not one of the values accepted for Enum class: [SUSI, PYEONIP, JEONGSI, JAEOEGUGMIN]", description = "validation에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@GetMapping()
	public List<QuestionResponse> getQuestions(@Valid @ModelAttribute FindQuestionsRequest request) {
		return questionService.getQuestions(request.type(), request.category());
	}

	@Operation(summary = "질문 자동 완성", description = "질문을 자동완성 시키는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 자동완성 성공")
	})
	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "Invalid input format: JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionType` from String \\\"SUSI 또는 PYEONIP 또는 JEONGSI 또는 JAEOEGUGMIN\\\": not one of the values accepted for Enum class: [SUSI, PYEONIP, JEONGSI, JAEOEGUGMIN]", description = "validation에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@GetMapping("/search")
	public SliceQuestionResponse<SearchedQuestionsResponse> searchQuestions(
		@Valid @ModelAttribute SearchQuestionsRequest request) {
		return questionService.searchQuestionsOfCursorPaging(request.content(), request.cursorViewCount(),
			request.questionId(), request.size());
	}
}