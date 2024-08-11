FROM nginx:latest

# 기본 설정 파일 제거
RUN rm /etc/nginx/conf.d/default.conf

# 사용자 정의 설정 파일 복사
COPY default.conf /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/nginx.conf