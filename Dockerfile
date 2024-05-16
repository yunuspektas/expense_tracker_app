# Temel image olarak OpenJDK 17 kullanıyoruz
FROM openjdk:17-jdk-slim

# Uygulamanın çalıştırılacağı dizini oluşturuyoruz
VOLUME /tmp

# Uygulama jar dosyasını container içine kopyalıyoruz
COPY out/artifacts/expense_tracker_app_jar/expense-tracker-app.jar app.jar

# Uygulamayı çalıştırmak için gerekli komutları belirtiyoruz
ENTRYPOINT ["java", "-jar", "/app.jar"]
