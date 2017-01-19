# Your Microservice Usage Example

### Overview:
This Your MicroService, provides a simple Template to bootstrap your first Microservice.

## Local Setup

### Pre-requisites:
1. Java [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)


### Building:
To run simple modify the supplied script:

    #!/bin/bash
    # ***************************************************
    # Build Current Branch.
    # ***************************************************
    cd <DIRECTORY>
    #
    git status
    #
    PROPERTIES="-Dsomething=true"
    PROPERTIES="${PROPERTIES} -Djava.net.preferIPv4Stack=true"
    #
    #
    mvn ${PROPERTIES} \
         clean verify
    #    clean verify -Dmaven.test.skip=true
    


## References:
