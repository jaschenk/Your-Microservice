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



## Errata
* Applied workaround for issue with Spring Boot repackaging and Failsafe: 
  https://github.com/spring-projects/spring-boot/issues/6254

## Acknowledgements
* Initial IdP code forked from: https://github.com/brahalla/Cerberus


