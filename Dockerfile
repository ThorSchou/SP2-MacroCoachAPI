# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml ./
COPY src ./src
# Build shaded jar (no tests for speed; enable if you want)
RUN mvn -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copy the fat jar produced by the shade plugin
COPY --from=build /build/target/app.jar /app/app.jar

# Optional: Javalin listens on 7000 in your local dev; inside compose it doesn’t matter
EXPOSE 7000
ENV JAVA_OPTS=""

# Let Javalin bind to 0.0.0.0 and read PORT if present
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
