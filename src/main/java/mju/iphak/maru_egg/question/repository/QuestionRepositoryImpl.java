package mju.iphak.maru_egg.question.repository;

import static com.querydsl.core.types.dsl.Expressions.*;
import static mju.iphak.maru_egg.question.domain.QQuestion.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<List<QuestionCore>> searchQuestionsByContentTokenAndType(final String contentToken,
		final QuestionType type) {
		BooleanTemplate matchExpression = booleanTemplate("function('match_against', {0}, {1}) > 0", question.content,
			contentToken);

		List<Tuple> tuples = queryFactory.select(question.id, question.contentToken)
			.from(question)
			.where(matchExpression.and(question.questionType.eq(type)))
			.fetch();

		List<QuestionCore> result = tuples.stream()
			.map(tuple -> QuestionCore.of(tuple.get(question.id), tuple.get(question.contentToken)))
			.collect(Collectors.toList());

		return Optional.of(result);
	}

	@Override
	public Optional<List<QuestionCore>> searchQuestionsByContentTokenAndTypeAndCategory(final String contentToken,
		final QuestionType type, final QuestionCategory category) {
		NumberTemplate<Double> booleanTemplate = Expressions.numberTemplate(Double.class, "function('match', {0}, {1})",
			question.content,
			contentToken);

		List<Tuple> tuples = queryFactory.select(question.id, question.contentToken)
			.from(question)
			.where(
				booleanTemplate.gt(0))
			.fetch();

		List<QuestionCore> result = tuples.stream()
			.map(tuple -> QuestionCore.of(tuple.get(question.id), tuple.get(question.contentToken)))
			.collect(Collectors.toList());
		return Optional.of(result);
	}
}