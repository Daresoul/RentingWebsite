FROM azul/zulu-openjdk:21-latest
VOLUME /tmp

# Copy the fat jar (not the -plain one)
COPY build/libs/RentingWebsite-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /images

COPY images/* /images/

ENTRYPOINT ["java","-jar","/app.jar"]


# ./gradlew clean build
# build with "docker build --platform=linux/amd64 -t kebabdk400/renting_service:latest .
# docker push kebabdk400/renting_service:latest