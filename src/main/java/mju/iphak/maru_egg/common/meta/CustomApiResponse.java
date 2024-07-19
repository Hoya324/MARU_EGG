package mju.iphak.maru_egg.common.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CustomApiResponses.class)
public @interface CustomApiResponse {
	String error() default "RuntimeException";

	int status() default 500;

	String message() default "서버에 오류가 발생했습니다. 담당자에게 연락주세요.";

	String description() default "내부 서버 오류";

	boolean isArray() default false;
}