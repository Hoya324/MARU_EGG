package mju.iphak.maru_egg.question.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.application.QuestionProcessingService;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.docs.QuestionControllerDocs;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.FindQuestionsRequest;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.request.SearchQuestionsRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionListItemResponse;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/questions")
public class QuestionController implements QuestionControllerDocs {

	private final QuestionProcessingService questionProcessingService;
	private final QuestionService questionService;

	@PostMapping()
	public QuestionResponse question(@Valid @RequestBody QuestionRequest request) {
		return questionProcessingService.question(request.type(), request.category(), request.content());
	}

	@PostMapping("/new")
	public void createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
		questionService.createQuestion(request);
	}

	@GetMapping()
	public List<QuestionListItemResponse> getQuestions(@Valid @ModelAttribute FindQuestionsRequest request) {
		return questionService.getQuestions(request.type(), request.category());
	}

	@GetMapping("/search")
	public SliceQuestionResponse<SearchedQuestionsResponse> searchQuestions(
		@Valid @ModelAttribute SearchQuestionsRequest request) {
		return questionService.searchQuestionsOfCursorPaging(request.content(), request.cursorViewCount(),
			request.questionId(), request.size());
	}
}