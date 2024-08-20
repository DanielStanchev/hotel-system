FROM amazoncorretto:21-alpine

WORKDIR /app

COPY rest/target/rest-0.0.1-SNAPSHOT.jar /app/hotel.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/hotel.jar"]

#docker file for image creation
