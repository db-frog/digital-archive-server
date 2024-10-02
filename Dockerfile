FROM gcr.io/distroless/java21-debian12:latest-amd64
WORKDIR /app
COPY target/*-jar-with-dependencies.jar app.jar
ENV JAVA_OPTS="-Dlogback.configurationFile=logback.xml"
ENV TZ="America/Los_Angeles"
EXPOSE 8080
USER nonroot
CMD [ "app.jar" ]