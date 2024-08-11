FROM nginx:latest

# Remove the default configuration
RUN rm /etc/nginx/conf.d/default.conf

# Copy the custom configurations
COPY default.conf /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/nginx.conf