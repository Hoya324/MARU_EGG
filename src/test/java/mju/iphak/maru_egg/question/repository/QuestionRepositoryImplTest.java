package mju.iphak.maru_egg.question.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.RepositoryTest;
import mju.iphak.maru_egg.question.dao.request.QuestionCoreDAO;
import mju.iphak.maru_egg.question.dao.response.QuestionCore;
import mju.iphak.maru_egg.question.domain.QQuestion;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;

class QuestionRepositoryImplTest extends RepositoryTest {

	@Autowired
	private QuestionRepositoryImpl questionRepositoryImpl;

	@PersistenceContext
	EntityManager em;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private JPAQueryFactory queryFactory;

	private Question question;
	private Answer answer;

	@BeforeEach
	public void setUp() throws Exception {
		queryFactory = new JPAQueryFactory(em);
		jdbcTemplate.execute("ALTER TABLE questions ADD FULLTEXT INDEX idx_ft_question_content(content)");
		question = questionRepository.findById(1L).orElseThrow();
		answer = Answer.of(question, "테스트 답변입니다.");
		answerRepository.save(answer);
	}

	@DisplayName("[성공] 내용토큰과 유형으로 질문을 검색에 성공한다.")
	@Test
	void 성공_내용토큰과_유형으로_질문_검색() {
		// given
		QQuestion question = QQuestion.question;
		String contentToken = "수시 입학 요강 대해";
		AdmissionType type = AdmissionType.SUSI;
		NumberTemplate<Double> numberTemplate = createBooleanTemplateByContentToken(question, contentToken);

		// when
		List<Question> results = executeSearchQuestions(numberTemplate, type);

		// then
		assertThat(results).isNotEmpty();
		assertThat(results.get(0).getContent()).contains("수시 입학 요강");
	}

	@DisplayName("[실패] 잘못된 내용토큰과 유형으로 질문 검색에 실패한다.")
	@Test
	void 실패_잘못된_내용토큰과_유형으로_질문_검색() {
		// given
		String invalidContentToken = "잘못된 질문";
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;
		QuestionCoreDAO questionCoreDAO = createInvalidQuestionCoreDAO(invalidContentToken, type, category);

		// when
		Optional<List<QuestionCore>> result = executeInvalidSearchQuestions(questionCoreDAO);

		// then
		assertThat(result).isPresent();
		assertThat(result.get()).isEmpty();
	}

	private List<Question> executeSearchQuestions(NumberTemplate<Double> numberTemplate, AdmissionType type) {
		return queryFactory.selectFrom(QQuestion.question)
			.where(numberTemplate.gt(0.0).or(numberTemplate.eq(0.0)).and(QQuestion.question.admissionType.eq(type)))
			.fetch();
	}

	private Optional<List<QuestionCore>> executeInvalidSearchQuestions(QuestionCoreDAO questionCoreDAO) {
		return questionRepositoryImpl.searchQuestions(questionCoreDAO);
	}

	private QuestionCoreDAO createInvalidQuestionCoreDAO(String invalidContentToken, AdmissionType type,
		AdmissionCategory category) {
		String content = "수시 일정 알려주세요.";
		QuestionRequest request = new QuestionRequest(type, category, content);
		return QuestionCoreDAO.of(request, invalidContentToken);
	}

	private NumberTemplate<Double> createBooleanTemplateByContentToken(QQuestion question, String content) {
		return Expressions.numberTemplate(Double.class, "function('match', {0}, {1})", question.content, content);
	}
}
