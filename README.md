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


```

git clone https://github.com/suvera/scim2-compliance-test-utility.git

cd scim2-compliance-test-utility

mvn clean install

java -Dserver.port=8080 -jar target/scim2-compliance-test-utility-0.0.1-SNAPSHOT.jar

# Logs
2020-09-09 13:06:12.567  INFO 20316 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer 
 : Tomcat started on port(s): 8080 (http) with context path ''
2020-09-09 13:06:12.578  INFO 20316 --- [  restartedMain] d.s.o.scim2.compliance.Scim2Application  
: Started Scim2Application in 2.735 seconds (JVM running for 3.489)


```


## Screenshots


![Service Screenshot](https://suvera.github.io/assets/images/scim_screenshot1.png)



## Do you want to develop SCIM 2.0 Server/Client?

Here is the JAVA SDK https://github.com/suvera/scim2-sdk
