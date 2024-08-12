FROM nginx:1.27.0

# Remove the default configuration
RUN rm -rf /etc/nginx/conf.d/default.conf
RUN rm -rf /etc/nginx/nginx.conf

COPY nginx.conf /etc/nginx/nginx.conf

ENTRYPOINT ["nginx", "-g", "daemon off;"]