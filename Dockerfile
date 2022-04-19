FROM openjdk:8-jdk-alpine
ADD target/database-service.jar database-service.jar

#exposing ports
EXPOSE 8002
EXPOSE 80
EXPOSE 3306

ENV JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk
ENV ACTIVATED_PROFILE=dev

ENV DB_URL=jdbc:mysql://localhost:3306/assignment_db
ENV DB_USER_NAME=admin
ENV DB_PASSWORD=1qaz@WSX3edc

ENV CONSUMER_TOPIC=sensor-process
ENV POOLING_TIME-OUT=1000
ENV STOPPING_THRESHOLD=100

ENTRYPOINT ["java","-jar","database-service.jar"]