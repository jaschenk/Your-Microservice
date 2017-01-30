# Your Microservice
Currently a Work in progress ...

This project provides an approach so that you can easily create various Microservices
for 'Your' Eco-System using a Common core.
This project allows you to wrap those specific individual Microservices
with a Standards Framework to bootstrap 'Your' Eco-System.  

These standard facilities can be shared 
throughout your Eco-System Product Suite for Developing, Building and Deploying 
Microservices Architecture Services and Various Components.


## Overview:
Your Microservice ...

## Local Setup

### Pre-requisites:

1. Java [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
2. Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files for Java 8 should be
applied.  [Download Here.](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
3. Maven


## Components:
Your Microservice is comprised of the following primary components:
* Your Microservice Annotation Definition
* Your Microservice Configuration to back our Annotation Definition
* IdP -- Identity Provider, Full JWT Token Based Authentication.
  * Persistent Store -- Using H2 In Memory Database for Testing and Example Usage.
   Any JDBC Compliant Database is acceptable.
  * Data Model
  * ..
* Common Services
  * Bulletin
  * Pulse
* REST Client to Interact with Other Your Microservice Instances.


### Building
To Build this project, simple ensure your have the above Pre-requisites and
 issue the following Maven command to build:
   ```
        mvn clean verify package
   ```  


### Running Your Microservice Example
Once the project has been built, simple change to the **your-microservice-example** 
directory contained in the project and proceed depending upon your applicable Operating System. 

Using the default settings, you will bootstrap the example Microservice on Port **9090**.

#### *NIX
   ```
    $  cd your-microservice-example
    $  ./bin/run_your_microservice_example.sh
   ```


#### WIN
   ```
    $  cd your-microservice-example
    $  bin\run_your_microservice_example.bat
   ```


## Errata
* Applied workaround for issue with Spring Boot repackaging and Failsafe: 
  https://github.com/spring-projects/spring-boot/issues/6254
  
* SSL has not been configured, using any type of Token Based Authentication Requires SSL!  
  If you are intending on running this or any variant, you MUST CONFIGURE and RUN SSL!


## Acknowledgements
* Initial IdP code forked from: https://github.com/brahalla/Cerberus


