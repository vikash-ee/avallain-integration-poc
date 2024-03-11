FROM openjdk:20-jdk
VOLUME /tmp
COPY target/hosting-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]