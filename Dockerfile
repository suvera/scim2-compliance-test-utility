FROM maven:3.8.5-openjdk-17

COPY . /build

WORKDIR /build

RUN mvn clean install \
    && jar_file=$(find target -name "*.jar" | head -n 1) \
    && mv $jar_file /bin/binary.jar \
    && rm -rf /build/* && rm -rf /root/.m2

EXPOSE 8081
CMD [ "bash", "-c" , "java -Dserver.port=8081 -jar /bin/binary.jar" ]
