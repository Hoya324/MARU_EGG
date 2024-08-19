package mju.iphak.maru_egg.question.repository;

import static mju.iphak.maru_egg.question.domain.QQuestion.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.domain.QQuestion;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

	private static final double MIN_MATCHING_POINT = 0.005;

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<List<QuestionCore>> searchQuestionsByContentTokenAndType(final String contentToken,
		final QuestionType type) {
		NumberTemplate<Double> booleanTemplate = createBooleanTemplate(question, contentToken);

		List<Tuple> tuples = queryFactory.select(question.id, question.contentToken)
			.from(question)
			.where(booleanTemplate.gt(0).and(question.questionType.eq(type)))
			.fetch();

		List<QuestionCore> result = tuples.stream()
			.map(tuple -> QuestionCore.of(tuple.get(question.id), tuple.get(question.contentToken)))
			.collect(Collectors.toList());

		return Optional.of(result);
	}

	@Override
	public Optional<List<QuestionCore>> searchQuestionsByContentTokenAndTypeAndCategory(final String contentToken,
		final QuestionType type, final QuestionCategory category) {
		NumberTemplate<Double> booleanTemplate = createBooleanTemplate(question, contentToken);

		List<Tuple> tuples = queryFactory.select(question.id, question.contentToken)
			.from(question)
			.where(
				booleanTemplate.gt(0).and(question.questionType.eq(type)).and(question.questionCategory.eq(category)))
			.fetch();

		List<QuestionCore> result = tuples.stream()
			.map(tuple -> QuestionCore.of(tuple.get(question.id), tuple.get(question.contentToken)))
			.collect(Collectors.toList());
		return Optional.of(result);
	}

	@Override
	public SliceQuestionResponse<SearchedQuestionsResponse> searchQuestionsOfCursorPagingByContentWithFullTextSearch(
		final String content,
		final Integer cursorViewCount, final Long questionId, final Pageable pageable) {
		QQuestion question = QQuestion.question;
		int pageSize = pageable.getPageSize();

		Integer ViewCountCursorKey =
			cursorViewCount != null && cursorViewCount > 0 ? cursorViewCount : Integer.MAX_VALUE;
		Long QuestionIdCursorKey = questionId != null && questionId > 0 ? questionId : Long.MAX_VALUE;

		NumberTemplate<Double> booleanTemplate = createBooleanTemplate(question, content);
		BooleanExpression cursorPredicate = createCursorPredicate(ViewCountCursorKey, QuestionIdCursorKey, question);

		List<Question> questions = fetchQuestions(booleanTemplate, cursorPredicate, question, pageSize);

		boolean hasNext = questions.size() > pageSize;

		if (hasNext) {
			questions.remove(pageSize);
		}

		List<SearchedQuestionsResponse> questionResponses = mapToResponse(questions);

		Integer nextCursorViewCount = null;
		Long nextQuestionId = null;
		if (!questions.isEmpty()) {
			Question lastQuestion = questions.get(questions.size() - 1);
			nextCursorViewCount = lastQuestion.getViewCount();
			nextQuestionId = lastQuestion.getId();
		}

		return new SliceQuestionResponse<>(questionResponses, pageable.getPageNumber(), pageSize, hasNext,
			nextCursorViewCount, nextQuestionId);
	}

	@Override
	public SliceQuestionResponse<SearchedQuestionsResponse> searchQuestionsOfCursorPagingByContentWithLikeFunction(
		final String content,
		final Integer cursorViewCount, final Long questionId, final Pageable pageable) {
		QQuestion question = QQuestion.question;
		int pageSize = pageable.getPageSize();

		List<Question> questions = queryFactory
			.selectFrom(question)
			.where(question.content.contains(content))
			.limit(pageSize)
			.fetch();

		boolean hasNext = questions.size() > pageSize;

		if (hasNext) {
			questions.remove(pageSize);
		}

		List<SearchedQuestionsResponse> questionResponses = mapToResponse(questions);

		Integer nextCursorViewCount = null;
		Long nextQuestionId = null;
		if (!questions.isEmpty()) {
			Question lastQuestion = questions.get(questions.size() - 1);
			nextCursorViewCount = lastQuestion.getViewCount();
			nextQuestionId = lastQuestion.getId();
		}

		return new SliceQuestionResponse<>(questionResponses, pageable.getPageNumber(), pageSize, hasNext,
			nextCursorViewCount, nextQuestionId);
	}

	private NumberTemplate<Double> createBooleanTemplate(QQuestion question, String content) {
		return Expressions.numberTemplate(Double.class, "function('match', {0}, {1})", question.content, content);
	}

	private BooleanExpression createCursorPredicate(Integer cursorViewCount, Long questionId, QQuestion question) {
		if (cursorViewCount != null && questionId != null) {
			return question.viewCount.lt(cursorViewCount)
				.or(question.viewCount.eq(cursorViewCount).and(question.id.lt(questionId)));
		} else if (cursorViewCount != null) {
			return question.viewCount.lt(cursorViewCount);
		} else if (questionId != null) {
			return question.id.lt(questionId);
		}
		return null;
	}

	private List<Question> fetchQuestions(NumberTemplate<Double> booleanTemplate, BooleanExpression cursorPredicate,
		QQuestion question, int pageSize) {
		return queryFactory.selectFrom(question)
			.where(booleanTemplate.gt(MIN_MATCHING_POINT), cursorPredicate)
			.orderBy(question.viewCount.desc(), question.id.desc())
			.limit(pageSize + 1)
			.fetch();
	}

	private List<SearchedQuestionsResponse> mapToResponse(List<Question> questions) {
		return questions.stream()
			.map(q -> SearchedQuestionsResponse.of(q.getId(), q.getContent()))
			.collect(Collectors.toList());
	}
}