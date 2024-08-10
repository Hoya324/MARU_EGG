FROM nginx:latest
# 기본 설정 파일 삭제
RUN rm /etc/nginx/conf.d/default.conf
# 작성한 설정 파일을 올바른 위치에 복사
COPY nginx.conf /etc/nginx/nginx.conf