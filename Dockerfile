# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build
 
WORKDIR /app
 
COPY pom.xml .
COPY src ./src
 
RUN mvn clean package -DskipTests
 
# Stage 2: Run the application
FROM tomcat:10-jdk21
 
WORKDIR /usr/local/tomcat/webapps
 
COPY --from=build /app/target/*.war ROOT.war
 
EXPOSE 8080
 
CMD ["catalina.sh", "run"]