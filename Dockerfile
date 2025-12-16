# 多阶段构建 Spring Boot 服务
# 构建阶段
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/services-0.1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]