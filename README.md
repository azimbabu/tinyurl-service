# Introduction

A RESTful Service to generate short url. 

# Requirements 
- Given a long url, generate a short and unique alias of it.
- Users can optionally provide short alias to use if it's not already used.
- Each short url has an expiration of 30 days by default. 
- Users can optionally override the expiration upto 365 days. 
- Each generated short url is 7 characters in length. This service can generate up to 64 ^ 7 short urls.
- Users are redirected to the original URL when they hit the short url.
- Provide top 10 short url redirection statistics : daily, weekly and monthly [TBD]
- Generated short aliases should not be predictable.
- Provide all the functionalities as REST endpoints.

# Software needed
- Gradle (https://gradle.org/). I used version 6.0.1 of gradle.
- Java (https://openjdk.java.net/projects/jdk/12/). I used version 12.
- Spring Boot (https://spring.io/projects/spring-boot). I used version 2.2.4.RELEASE.
- Apache Cassandra (http://cassandra.apache.org/). I used version 3.11.4.

# System Architecture
TBD

# Algorithm
TBD

# Build
##Prerequisites
- Download apache cassandra from http://cassandra.apache.org/download/. Follow your platform specific instructions to install.
- Start cassandra and make sure it's running locally on port 9042.
- Connect to cassandra using cqlsh and create a new keyspace named tinyurl_service using this CQL statement:
```
CREATE KEYSPACE tinyurl_service WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1} AND DURABLE_WRITES = true;
```
## Run using gradle
To run the service from command line:
```
$ ./gradlew bootRun
```
In a separate terminal window:
```
$ curl http://localhost:8080/actuator/health
  
{"status":"UP"}
```
## Run as executable JAR
Build the JAR file:
```
$ ./gradlew build
```
Run the JAR file:
```
$ java -jar build/libs/tinyurl-service-0.0.1-SNAPSHOT.jar
```

## Run using IDE
You can also import the project in your favorite IDE such as IntelliJ or Eclipse and run it as a Spring Boot application.

# TODO
- Add Integration and API tests.
- Dockerize the service.
- Add swagger documentation.
- Add statistics functionality.





