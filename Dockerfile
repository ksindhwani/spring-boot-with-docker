FROM openjdk:8
EXPOSE 8080
WORKDIR /app

# Copy maven executable from directory to container 
COPY mvnw .
COPY .mvn .mvn

# Copy pom.xml 
COPY pom.xml .

# COPY Project Source
COPY ./src ./src
COPY ./pom.xml ./pom.xml

# Making the mvnw executable in container
RUN chmod 755 /app/mvnw

# Downloading all dependencies
RUN ./mvnw dependency:go-offline -B

#Creating application .jar
RUN ./mvnw package -DskipTests

RUN  ls -al
ENTRYPOINT [ "java","-jar","target/personio-interview.jar" ]