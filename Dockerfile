FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/core/target/*.jar app.jar

# Environment variables (can be overridden at runtime)
ENV DB_URL="jdbc:h2:file:/data/mydb" \
    FILE_SAVE_PATH="/data/downloads" \
    LOG_FILE_PATH="/data/logs" \
    JDA_API_KEY="" \
    COMMAND_PREFIX="%" \
    JAVA_OPTS="-XX:+UseContainerSupport -Xmx512m"

VOLUME /data
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# ----------------------------------------------------------
# Build and Run Instructions (for nerdctl)
# ----------------------------------------------------------
# Build the image:
#   nerdctl build -t sarah-bot .
#
# Run the container (with environment variables):
#   nerdctl run -d --name sarah-bot \
#     -e JDA_API_KEY=your_key \
#     -e DB_URL=jdbc:h2:file:/data/mydb \
#     -e FILE_SAVE_PATH=/data/downloads \
#     -e LOG_FILE_PATH=/data/logs \
#     -v /host/data:/data \
#     sarah-bot
#