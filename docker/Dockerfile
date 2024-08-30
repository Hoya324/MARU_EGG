FROM openjdk:17

EXPOSE 8080

# JAR 파일 경로를 명시적으로 수정
ARG JAR_FILE=docker/build/libs/*.jar

# 애플리케이션의 JAR 파일을 컨테이너에 추가
COPY ${JAR_FILE} MARU_EGG_BE.jar

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "MARU_EGG_BE.jar"]