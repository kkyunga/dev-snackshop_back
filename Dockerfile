FROM eclipse-temurin:17-alpine
WORKDIR /app
# 이미 서버에 올려둔 jar 파일을 컨테이너 안으로 복사
COPY dev-snackshop_back-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# 서버 프로필 적용해서 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=server"]