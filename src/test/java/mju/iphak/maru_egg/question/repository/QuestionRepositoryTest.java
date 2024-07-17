package mju.iphak.maru_egg.question.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.RepositoryTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

class QuestionRepositoryTest extends RepositoryTest {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	private Question question;
	private Answer answer;

	@BeforeEach
	public void setUp() throws Exception {
		question = new Question("테스트 질문입니다.", "테스트 질문", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE, 0);
		questionRepository.save(question);

		answer = Answer.of(question, "테스트 답변입니다.");
		answerRepository.save(answer);
	}

	@DisplayName("질문을 조회하는데 설공")
	@Test
	void 질문_조회_성공() {
		// given
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;

		// when
		List<Question> questions = questionRepository.findAllByQuestionTypeAndQuestionCategory(
			type, category);

		// then
		assertThat(questions).isNotEmpty();
		assertThat(questions).isEqualTo(List.of(question));
	}

	@DisplayName("질문을 조회하는데 실패한 경우-type, category 를 못 찾은 경우")
	@Test
	void 질문_조회_실패() {
		// given
		QuestionType invalidType = QuestionType.JEONGSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;

		// when
		List<Question> questions = questionRepository.findAllByQuestionTypeAndQuestionCategory(
			invalidType, category);

		// then
		assertThat(questions.isEmpty()).isTrue();
	}

	@DisplayName("질문을 조회하는데 성공 - category 없이 type으로 조회")
	@Test
	void 질문_조회_성공_카테고리_없이() {
		// given
		QuestionType type = QuestionType.SUSI;

		// when
		List<Question> questions = questionRepository.findAllByQuestionType(type);

		// then
		assertThat(questions).isNotEmpty();
		assertThat(questions).isEqualTo(List.of(question));
	}

	@DisplayName("질문을 조회하는데 실패한 경우 - type만으로 조회할 때")
	@Test
	void 질문_조회_실패_타입만으로() {
		// given
		QuestionType invalidType = QuestionType.JEONGSI;

		// when
		List<Question> questions = questionRepository.findAllByQuestionType(invalidType);

		// then
		assertThat(questions.isEmpty()).isTrue();
	}
}