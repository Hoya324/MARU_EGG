package mju.iphak.maru_egg.question.api.swagger;

import java.util.List;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.dto.request.FindQuestionsRequest;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.request.SearchQuestionsRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionListItemResponse;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

@Tag(name = "Question API", description = "질문 관련 API 입니다.")
public interface QuestionControllerDocs {

	@Operation(summary = "질문 요청", description = "질문하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 성공")
	})
	QuestionResponse question(@Valid @RequestBody QuestionRequest request);

	@Operation(summary = "질문 목록 요청", description = "질문 목록을 보내주는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 성공")
	})
	List<QuestionListItemResponse> getQuestions(@Valid @ModelAttribute FindQuestionsRequest request);

	@Operation(summary = "질문 자동 완성", description = "질문을 자동완성 시키는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 자동완성 성공")
	})
	SliceQuestionResponse<SearchedQuestionsResponse> searchQuestions(
		@Valid @ModelAttribute SearchQuestionsRequest request);

	@Operation(summary = "질문 id로 검색", description = "질문 id로 검색 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 id로 검색 성공")
	})
	QuestionResponse getQuestion(@Valid @RequestParam Long questionId);
}