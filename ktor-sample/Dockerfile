# Build the application
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

RUN chmod +x gradlew

# copy source and build fat jar
COPY src src
RUN ./gradlew buildFatJar --no-daemon

# Run the application
FROM eclipse-temurin:17-jre AS runner
WORKDIR /app
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/ktor-app.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-app.jar"]