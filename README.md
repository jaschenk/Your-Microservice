# Your Microservice Overview
This project provides an approach so that you can easily create various Microservices
for 'Your' Eco-System using a Common core.
This project allows you to wrap those specific individual Microservices
with a Standards Framework to bootstrap 'Your' Eco-System.  

These standard facilities can be shared 
throughout your Eco-System Product Suite for Developing, Building and Deploying 
Microservices Architecture Services and Various Components.

The one nice feature is that Security with a IdP using JWTs is first and included in this core.

## Local Setup
Simply ensure you have the necessary Pre-requisites and you should be good to go...

### Pre-requisites

1. Java [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
2. Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files for Java 8 should be
applied.  [Download Here.](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
3. Maven


## Components
Your Microservice is comprised of the following primary components:
* Your Microservice Annotation Definition.
* Your Microservice Configuration to back our Annotation Definition
* IdP -- Identity Provider, Full JWT Token Based Authentication.
  * Persistent Store -- Using H2 In Memory Database for Testing and Example Usage.
   Any JDBC Compliant Database is acceptable.
* Common Services
  * Bulletin -- Provides ability to inject System Bulletin Updates via File System.  
  The Bulletin Service provides a means to respond to current state of runtime Application.
  * Pulse -- Provides simple status of current Runtime Application.
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

#### Default Data
To simulate some form of real Data, the H2 Database is seeded with a number of entries to support
an initial login to the back-end facilities.

Upon bootstrap of the Data Layer, the SQL Script located with the Core Resources to executed to Load the
 initial Data into the H2 database.
   ```
    db\h2\insert-data.sql
   ```

Remember, the **H2 Database** is an In-memory database, so upon subsequent 
restarts, all data is cleared and initialized per above SQL script.

For a Production implementation, you would remove this initialization capability to use an
established Database.


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

### Your Microservice Domain Model
The Your Microservice Domain Model provides a simple but extensible Data Model to represent Entities, Entity Roles

![Your Microservice Domain Model](https://github.com/jaschenk/Your-Microservice/tree/develop/doc/images/YourMicroserviceIdP_JPADiagram.png)

### Interfacing with IdP
* ...
* ...


## ToDo's
* Orchestration Layer -- Build out the Orchestration layer to provide 
Microservices to work in concert with other Microservices.


## Errata
* Applied workaround for issue with Spring Boot repackaging and Failsafe: 
  https://github.com/spring-projects/spring-boot/issues/6254
  
* SSL has not been configured, using any type of Token Based Authentication Requires SSL!  
  If you are intending on running this or any variant, you MUST CONFIGURE and RUN SSL!

## Acknowledgements
* Initial IdP code forked from: https://github.com/brahalla/Cerberus


