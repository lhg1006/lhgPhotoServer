# 기반 이미지로 Java 8을 사용합니다.
FROM openjdk:8-jdk-alpine

# 애플리케이션 JAR 파일을 /app 디렉토리에 복사합니다.
COPY target/your-spring-boot-app.jar /app/your-spring-boot-app.jar

# 컨테이너 내에서 사용할 작업 디렉토리를 설정합니다.
WORKDIR /app

# 애플리케이션을 실행합니다.
CMD ["java", "-jar", "your-spring-boot-app.jar"]