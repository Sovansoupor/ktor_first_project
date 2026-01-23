# ----------- Stage 1: Build ----------- #
FROM eclipse-temurin:17-jdk AS build

WORKDIR /src
COPY . .

RUN chmod +x ./gradlew && ./gradlew clean shadowJar -x test --no-daemon

# ----------- Stage 2: Runtime ----------- #
FROM eclipse-temurin:17-jre
EXPOSE 8080
COPY --from=build /src/build/libs/*-all.jar /app/ktor-app.jar

ENTRYPOINT ["sh","-c","exec java -Dio.ktor.deployment.host=0.0.0.0 -Dio.ktor.deployment.port=${PORT:-8081} -jar /app/ktor-app.jar"]
