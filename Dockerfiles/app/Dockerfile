FROM maven:3.9.9-eclipse-temurin-21-jammy AS BUILD_IMAGE

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM tomcat:10-jdk21

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=BUILD_IMAGE /app/target/ecommerce-v1.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]