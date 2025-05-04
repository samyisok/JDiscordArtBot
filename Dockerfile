FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/core/target/*.jar app.jar
ENV DB_URL=jdbc:h2:file:/data/mydb \
    FILE_SAVE_PATH=/data/downloads \
    LOG_FILE_PATH=/data/logs \
    JAVA_OPTS="-XX:+UseContainerSupport -Xmx512m"
VOLUME ["/data"]
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]