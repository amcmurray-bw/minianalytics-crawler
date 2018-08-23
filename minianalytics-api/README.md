# API (Mini Analytics)

This is the API for minianalytics. It consists of two main parts, Queries and Mentions. 
Queries are created by the user and then mentions are returned. 


### Running the tests

To run all the tests, run
```
mvn clean verify
```
to run only the unit tests 
```
mvn clean verify -DskipITs
or
mvn clean test
```
to run only the integration tests 
```
mvn clean verify -Dskip.surefire.tests
```
