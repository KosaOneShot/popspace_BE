# 1) Builder 스테이지: 표준 JDK (멀티아키 지원)
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Gradle Wrapper 복사 & 실행권한 부여
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew

# build.gradle, settings.gradle 복사 → 의존성 캐시
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon

# 전체 소스 복사 & JAR 생성
COPY . .
RUN ./gradlew bootJar -x test --no-daemon

# 2) Runtime 스테이지: 표준 JRE (멀티아키 지원)
FROM eclipse-temurin:17-jre

WORKDIR /app

# 빌더에서 만든 JAR만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-Xms256m","-Xmx1024m","-jar","app.jar"]
