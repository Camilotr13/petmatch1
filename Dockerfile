FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8092

CMD ["java", "-jar", "target/PetMatch-0.0.1-SNAPSHOT.jar"]