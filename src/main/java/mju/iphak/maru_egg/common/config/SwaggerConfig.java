package mju.iphak.maru_egg.common.config;

import java.lang.reflect.Method;
import java.util.Map;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import mju.iphak.maru_egg.common.exception.ErrorResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponses;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.components(new Components()
				.addSecuritySchemes("bearerAuth", new SecurityScheme()
					.type(Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
					.in(In.HEADER)
					.name("Authorization"))
				.addSchemas("ErrorResponse", createErrorResponseSchema()))
			.info(apiInfo())
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
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
			handleCustomApiResponses(apiResponses, handlerMethod);
			return operation;
		};
	}

	private void handleCustomApiResponses(ApiResponses apiResponses, HandlerMethod handlerMethod) {
		Method method = handlerMethod.getMethod();
		CustomApiResponses customApiResponses = method.getAnnotation(CustomApiResponses.class);
		if (customApiResponses != null) {
			for (CustomApiResponse customApiResponse : customApiResponses.value()) {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setDescription(customApiResponse.description());
				addContent(apiResponse, customApiResponse.error(), customApiResponse.status(),
					customApiResponse.message());
				apiResponses.addApiResponse(String.valueOf(customApiResponse.status()), apiResponse);
			}
		} else {
			CustomApiResponse customApiResponse = method.getAnnotation(CustomApiResponse.class);
			if (customApiResponse != null) {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setDescription(customApiResponse.description());
				addContent(apiResponse, customApiResponse.error(), customApiResponse.status(),
					customApiResponse.message());
				apiResponses.addApiResponse(String.valueOf(customApiResponse.status()), apiResponse);
			}
		}
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