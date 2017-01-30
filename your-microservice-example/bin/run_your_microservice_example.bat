@echo off
REM # Define Our SpringBoot JAR
SET YMSE_JAR=your-microservice-example-1.0.0.0-SNAPSHOT-exec.jar
SET DEBUG_PORT=9044
REM #
REM # ********************************
REM # Establish Runtime Properties
REM # ********************************
SET PROPERTIES=-DLOG_PATH=target
REM #
REM # ********************************
REM # Establish Runtime JAVA OPTS
REM # ********************************
REM #
SET JAVA_OPTS=-d64 -server -Xss4M -Xms8G -Xmx8G -Xmn2G -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=%DEBUG_PORT%
REM #
REM # Run Instance
"%JAVA_HOME%\bin\java" %PROPERTIES% %JAVA_OPTS% -jar target\%YMSE_JAR%
