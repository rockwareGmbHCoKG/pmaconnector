FROM timbru31/java-node:11-jdk-gallium

COPY docker-resources/configuration/ configuration/
ARG pasv_min_port
ARG pasv_max_port
ARG pasv_address
RUN printf "\npasv_min_port=${pasv_min_port}\npasv_max_port=${pasv_max_port}\npasv_address=${pasv_address}\n" >> /configuration/vsftpd.conf

RUN mkdir shared

ARG frontend_active
RUN echo enable-frontend-$frontend_active | awk '{print toupper($0)}' | xargs -I file_name touch configuration/file_name
COPY target/frontend-compressed.tar.gz frontend.tar.gz

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
CMD ["/configuration/start.sh"]