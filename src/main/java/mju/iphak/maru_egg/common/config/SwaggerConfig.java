package mju.iphak.maru_egg.common.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import mju.iphak.maru_egg.common.exception.ErrorResponse;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.components(new Components().addSchemas("ErrorResponse", createErrorResponseSchema()))
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("MARU EGG REST API")
			.description("명지대 입학처 챗봇, MARU EGG REST API 문서.")
			.version("1.0.0");
	}

	@Bean
	public OperationCustomizer operationCustomizer() {
		return (operation, handlerMethod) -> {
			ApiResponses apiResponses = operation.getResponses();
			if (apiResponses == null) {
				apiResponses = new ApiResponses();
				operation.setResponses(apiResponses);
			}
			apiResponses.putAll(getCommonResponses());
			return operation;
		};
	}

	private Map<String, ApiResponse> getCommonResponses() {
		LinkedHashMap<String, ApiResponse> responses = new LinkedHashMap<>();
		responses.put("404", notFoundResponse());
		responses.put("500", internalServerResponse());
		return responses;
	}

	private ApiResponse notFoundResponse() {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setDescription("Not Found - 요청한 URI가 올바른지 확인한다.");
		addContent(apiResponse, "EntityNotFoundException", 404,
			"type: SUSI, category: PAST_QUESTIONS, content: 수시 입학 요강에 대해 알려주세요.인 질문을 찾을 수 없습니다.");
		return apiResponse;
	}

	private ApiResponse internalServerResponse() {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setDescription("Internal Server Error (Unchecked Exception) - API 담당자에게 오류 확인을 요청한다.");
		addContent(apiResponse, "HttpMessageNotReadableException", 500,
			"JSON parse error: Cannot deserialize value of type `mju.iphak.maru_egg.question.domain.QuestionCategory` from String \\\"PAST_QUESTION\\\": not one of the values accepted for Enum class: [PAST_QUESTIONS, ETC, UNIV_LIFE, INTERVIEW_PRACTICAL_TEST, PASSING_RESULT, ADMISSION_GUIDELINE]");
		return apiResponse;
	}

	private void addContent(ApiResponse apiResponse, String error, int status, String message) {
		Content content = new Content();
		MediaType mediaType = new MediaType();
		Schema<ErrorResponse> schema = new Schema<>();
		schema.$ref("#/components/schemas/ErrorResponse");
		mediaType.schema(schema)
			.example(new ErrorResponse(error, status, message));
		content.addMediaType("application/json", mediaType);
		apiResponse.setContent(content);
	}

	private Schema<ErrorResponse> createErrorResponseSchema() {
		return new Schema<ErrorResponse>()
			.type("object")
			.properties(Map.of(
				"error", new Schema<String>().type("string"),
				"status", new Schema<Integer>().type("integer"),
				"message", new Schema<String>().type("string")
			));
	}
}