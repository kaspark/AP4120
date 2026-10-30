# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle/ gradle/
COPY src/ src/
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && mkdir -p /root/.gradle
RUN --mount=type=secret,id=gradle_properties,target=/root/.gradle/gradle.properties \
    ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:25-jre
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /src/build/libs/app.jar app.jar
EXPOSE 18440
ENV SPRING_PROFILES_ACTIVE=local
ENTRYPOINT ["java", "-jar", "app.jar"]
