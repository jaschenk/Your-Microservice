#!/bin/bash
#
# Define Our SpringBoot JAR
YMSE_JAR=your-microservice-example-1.0.0.1-SNAPSHOT-exec.jar
DEBUG_PORT=9044
#
# ********************************
# Establish Runtime Properties
# ********************************
PROPERTIES=""
PROPERTIES="${PROPERTIES} -DLOG_PATH=./"
#
# ********************************
# Establish Runtime JAVA OPTS
# ********************************
#
JAVA_OPTS="-server -Xss4M -Xms8G -Xmx8G -Xmn2G"
JAVA_OPTS="${JAVA_OPTS} -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=${DEBUG_PORT}"
#
# Run Instance
java ${PROPERTIES} ${JAVA_OPTS} -jar ./target/${YMSE_JAR}