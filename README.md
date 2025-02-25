# SCIM 2.0 Compliance Test Utility

System for Cross-domain Identity Management (**SCIM**) specification is designed to make managing 
user identities in cloud-based applications and services easier. The specification suite seeks to 
build upon experience with existing schemas and deployments, placing specific emphasis on 
simplicity of development and integration, while applying existing authentication, authorization, and 
privacy models. 

Its intent is to reduce the cost and complexity of user management operations by providing a common 
user schema and extension model, as well as binding documents to provide patterns for exchanging 
this schema using standard protocols. 
In essence: make it fast, cheap, and easy to move users in to, out of, and around the cloud.


**More Info on:**

http://www.simplecloud.info/

https://tools.ietf.org/html/rfc7644

https://tools.ietf.org/html/rfc7643


## Installation

Use docker or build your own jar


### via Docker

```
# on Linux/Windows
docker run -it --rm --name scim2-compliance-test-utility -p 8081:8081 suvera/scim2-compliance-test-utility:1.0.2

# on MAC OS (specify platform)
docker run -it --rm --name scim2-compliance-test-utility  -p 8081:8081 --platform linux/amd64 suvera/scim2-compliance-test-utility:1.0.2
```

Open Test utility in the browser
http://localhost:8081/


### Or, build your own JAR
```
git clone https://github.com/suvera/scim2-compliance-test-utility.git

cd scim2-compliance-test-utility

mvn clean install

java -Dserver.port=8081 -jar target/scim2-compliance-test-utility-1.0.2.jar

# Logs
2020-09-09 13:06:12.567  INFO 20316 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer 
 : Tomcat started on port(s): 8081 (http) with context path ''
2020-09-09 13:06:12.578  INFO 20316 --- [  restartedMain] d.s.o.scim2.compliance.Scim2Application  
: Started Scim2Application in 2.735 seconds (JVM running for 3.489)
```

Open Test utility in the browser
http://localhost:8081/


## Screenshots


![Service Screenshot](https://suvera.github.io/assets/images/scim_screenshot1.png)



## Do you want to develop SCIM 2.0 Server/Client?

Here is the JAVA SDK https://github.com/suvera/scim2-sdk
