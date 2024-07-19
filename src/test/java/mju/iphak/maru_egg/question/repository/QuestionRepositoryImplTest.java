package mju.iphak.maru_egg.question.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.RepositoryTest;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.domain.QQuestion;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

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

	@Autowired
	private JPAQueryFactory queryFactory;

	private Question question;
	private Answer answer;

	@BeforeEach
	public void setUp() throws Exception {
		String checkIndexQuery = "SHOW INDEX FROM questions WHERE Key_name = 'idx_ft_question_content'";
		List<?> result = jdbcTemplate.queryForList(checkIndexQuery);
		if (!result.isEmpty()) {
			jdbcTemplate.execute("ALTER TABLE questions DROP INDEX idx_ft_question_content");
		}

		jdbcTemplate.execute("ALTER TABLE questions ADD FULLTEXT INDEX idx_ft_question_content(content)");

		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, question_type, question_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			1L, "테스트 질문 예시 예시입니다.", "테스트 질문 예시", QuestionType.SUSI.name(), QuestionCategory.ADMISSION_GUIDELINE.name(),
			0);
		question = questionRepository.findById(1L).orElseThrow();

		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, question_type, question_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			2L, "추가1 테스트 질문 예시입니다.", "추가 테스트 질문 예시", QuestionType.SUSI.name(),
			QuestionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, question_type, question_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			3L, "추가2 테스트 질문 예시입니다.", "추가 테스트 질문 예시", QuestionType.SUSI.name(),
			QuestionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, question_type, question_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			4L, "추가3 테스트 질문 예시입니다.", "추가 테스트 질문 예시", QuestionType.SUSI.name(),
			QuestionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, question_type, question_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			5L, "추가4 테스트 질문 예시입니다.", "추가 테스트 질문 예시", QuestionType.SUSI.name(), QuestionCategory.ETC.name(), 0);

		answer = Answer.of(question, "테스트 답변입니다.");
		answerRepository.save(answer);
	}

	@DisplayName("contentToken, type으로 질문을 검색하는데 성공")
	@Test
	void contentToken_type_질문_검색_성공() {
		// given
		String contentToken = "테스트 질문 예시";
		QuestionType type = QuestionType.SUSI;

		// when
		Optional<List<QuestionCore>> result = questionRepositoryImpl.searchQuestionsByContentTokenAndType(contentToken,
			type);

		// then
		assertThat(result).isPresent();
		List<QuestionCore> questionCores = result.get();
		assertThat(questionCores).isNotEmpty();
		assertThat(questionCores.get(0).id()).isEqualTo(question.getId());
	}

	@DisplayName("contentToken, type으로 질문을 검색하는데 실패한 경우")
	@Test
	void contentToken_type으로_질문_검색_실패() {
		// given
		String invalidContentToken = "잘못된 질문";
		QuestionType type = QuestionType.SUSI;

		// when
		Optional<List<QuestionCore>> result = questionRepositoryImpl.searchQuestionsByContentTokenAndType(
			invalidContentToken, type);

		// then
		assertThat(result).isPresent();
		assertThat(result.get()).isEmpty();
	}

	@DisplayName("contentToken, type, Category로 질문을 검색하는데 성공")
	@Test
	void contentToken_type_Category로_질문_검색_성공() {
		// given
		String contentToken = "테스트 질문 예시";
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;

		// when
		Optional<List<QuestionCore>> result = questionRepositoryImpl.searchQuestionsByContentTokenAndTypeAndCategory(
			contentToken, type, category);

		// then
		assertThat(result).isPresent();
		List<QuestionCore> questionCores = result.get();
		assertThat(questionCores).isNotEmpty();
		assertThat(questionCores.get(0).id()).isEqualTo(question.getId());
	}

	@DisplayName("contentToken, type, Category로 질문을 검색하는데 실패한 경우")
	@Test
	void contentToken_type_Category로_질문_검색_실패() {
		// given
		String invalidContentToken = "잘못된 질문";
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;

		// when
		Optional<List<QuestionCore>> result = questionRepositoryImpl.searchQuestionsByContentTokenAndTypeAndCategory(
			invalidContentToken, type, category);

		// then
		assertThat(result).isPresent();
		assertThat(result.get()).isEmpty();
	}

	@DisplayName("content로 질문을 페이지네이션 조회 - 성공")
	@Test
	void content로_질문_페이지네이션_조회_성공() {
		// given
		String content = "테스트 질문 예시";
		Pageable pageable = PageRequest.of(0, 3);

		// when
		SliceQuestionResponse<SearchedQuestionsResponse> result = questionRepositoryImpl.searchQuestionsOfCursorPagingByContent(
			content, null, null, pageable);

		// then
		assertThat(result.data()).isNotEmpty();
		assertThat(result.data().size()).isEqualTo(3);
		assertThat(result.data().get(0).content()).contains(content);
	}

	@DisplayName("content로 질문을 페이지네이션 조회 - 실패")
	@Test
	void content로_질문_페이지네이션_조회_실패() {
		// given
		String content = "존재하지 않는 질문";
		Pageable pageable = PageRequest.of(0, 3);

		// when
		SliceQuestionResponse<SearchedQuestionsResponse> result = questionRepositoryImpl.searchQuestionsOfCursorPagingByContent(
			content, null, null, pageable);

		// then
		assertThat(result.data()).isEmpty();
	}

	@Test
	void contentToken_type() {
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		QQuestion question = QQuestion.question;
		String contentToken = "테스트 질문 예시";
		QuestionType type = QuestionType.SUSI;

		// List<String> results = queryFactory
		// 	.select(question.content)
		// 	.from(question)
		// 	.where(question.contentToken.contains(contentToken)
		// 		.and(question.questionType.eq(type)))
		// 	.fetch();
		NumberTemplate<Double> numberTemplate = Expressions.numberTemplate(Double.class,
			"function('match', {0}, {1})", question.content, contentToken);

		List<String> results = queryFactory.select(question.contentToken)
			.from(question)
			.where(
				numberTemplate.gt(0).and(question.questionType.eq(type)))
			.fetch();

		System.out.println(results);
		assertThat(results).isNotEmpty();
	}
}