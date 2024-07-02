FROM openjdk:8-jdk-alpine

WORKDIR /usr/src/app

COPY ./target/generator-1.0.jar ./app.jar

# Run the JAR file
CMD ["java", "-jar", "app.jar"]