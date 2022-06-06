FROM maven:3-openjdk-11

COPY . /build
COPY ./settings.xml /root/.m2/settings.xml

WORKDIR /build

RUN mvn clean install

EXPOSE 8080
CMD [ "bash", "-c" , "java -Dserver.port=8080 -jar target/scim2-compliance-test-utility-0.0.1-SNAPSHOT.jar" ]
