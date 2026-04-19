FROM maven:3.9.6-eclipse-temurin-22 AS builder
WORKDIR /temp
COPY pom.xml .
RUN mvn dependency:go-offline -B -q
COPY src ./src
RUN mvn clean package -q

FROM tomcat:jdk21-temurin-jammy
COPY --from=builder /temp/target/*.war /usr/local/tomcat/webapps/ticket-app.war
