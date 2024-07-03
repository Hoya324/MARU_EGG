package mju.iphak.maru_egg.question.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Question API", description = "질문 관련 API 입니다.")
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

	@GetMapping()
	public String getQuestion() {
		return "아 됐다.";
	}
}
