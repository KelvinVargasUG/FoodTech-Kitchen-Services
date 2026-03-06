# Build stage
FROM gradle:8.5-jdk17@sha256:f59836e46ad7a565813de06768ff2884700d12b7ceedacb1701a2983dc859010 AS build
WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build application
RUN ./gradlew clean bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine@sha256:2a8ebbb44f3fb44868ec754946730a7b9ed6d1d9f5e05e066c53916dc5498943
ARG VERSION="dev"
ARG VCS_REF="unknown"
ARG VCS_URL="unknown"

LABEL org.opencontainers.image.title="FoodTech Kitchen Service" \
			org.opencontainers.image.description="Spring Boot service for kitchen order orchestration" \
			org.opencontainers.image.version=$VERSION \
			org.opencontainers.image.revision=$VCS_REF \
			org.opencontainers.image.source=$VCS_URL

WORKDIR /app

RUN apk add --no-cache busybox-extras \
		&& addgroup -S app \
		&& adduser -S app -G app

# Copy jar from build stage
COPY --from=build --chown=app:app /app/build/libs/app.jar /app/app.jar

USER app

# Expose port
EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=25.0"

HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
	CMD nc -z 127.0.0.1 8080 || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
