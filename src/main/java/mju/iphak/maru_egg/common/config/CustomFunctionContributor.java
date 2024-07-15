package mju.iphak.maru_egg.common.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

public class CustomFunctionContributor implements FunctionContributor {

	private static final String FUNCTION_NAME = "match";
	private static final String FUNCTION_PATTERN = "MATCH (?1) AGAINST (?2 IN BOOLEAN MODE)";

	@Override
	public void contributeFunctions(final FunctionContributions functionContributions) {
		functionContributions.getFunctionRegistry()
			.registerPattern(FUNCTION_NAME, FUNCTION_PATTERN,
				functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.DOUBLE));
	}
}