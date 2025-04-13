FROM openjdk:24-jdk-slim

ARG SUPABASE_URL
ARG SUPABASE_KEY
ARG SUPABASE_BUCKET

ENV SUPABASE_URL=$SUPABASE_URL
ENV SUPABASE_KEY=$SUPABASE_KEY
ENV SUPABASE_BUCKET=$SUPABASE_BUCKET


WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

COPY src src

RUN chmod +x gradlew
RUN ./gradlew build -x test

EXPOSE 8080

CMD ["java", "-jar", "build/libs/image-upload-0.0.1-SNAPSHOT.jar"]