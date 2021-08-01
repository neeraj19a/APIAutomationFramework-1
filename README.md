# (API Automation Framework)

#  Technologies and Libraries Used

* [Java 8](https://www.oracle.com/sg/java/technologies/javase/javase-jdk8-downloads.html) as the programming language
* [TestNG](https://testng.org/doc/) as the UnitTest framework to support the test creation
* [JavaFaker](https://github.com/DiUS/java-faker) as the faker data generation strategy
* [Owner](http://owner.aeonbits.org/) to minimize the code to handle the properties file
* [REST ASSURED](https://rest-assured.io/) to test and validate REST services
* [GSON](https://github.com/google/gson) Java library to serialize and deserialize Java objects to JSON
* [JACKSON](https://github.com/FasterXML/jackson) JSON parser for Java
* [json-schema-validator](https://mvnrepository.com/artifact/io.rest-assured/json-schema-validator) to validate Schema
* [Hamcrest](http://hamcrest.org/) Hamcrest allows checking for conditions in your code via existing matchers classes. It also allows you to define your custom matcher implementations. 
* [EXTENT Report](https://www.extentreports.com/) For Reporting
* [Log4j](https://logging.apache.org/log4j/2.x/) For Logging
* [Lombok](https://projectlombok.org/) Lombok is used to reduce boilerplate code for model/data objects.it can generate getters and setters for those object automatically

#  Prerequisite
Java, Maven,Lombok should be installed on your machine.

#How to Install Lombok on IntelliJ
Refer--> https://projectlombok.org/setup/intellij

## About Framework:
This framework is built using RestAssured.

We use POJO with @Data and @Builder annotations provided by Lombok to create Data at runtime without Getter and Setters

## Important Classes ,Files and Folders:

**APIConstant:** This class defines all API Paths and possible CRUD Operations on REST API .

**ConfigurationManager:** This class is used to read Property Files **config.properties** ( it contains BASE URL of the API and Username and Password and schema filenames)

**extent-config.xml:** This file contains configuration for extent Report

**log4j.properties:** This contains configurations for Logging used by Log4j to generate Logs. 

**TestReport.html** This is the Extent Report that is generated when API Test Cases are run, It is present under path (/src/main/testReport/TestReport.html)

##POJO
All classes required for API are present under (/src/test/java/pojo)

##TestData
Test Data is getting generated in FakerUtils class(This is using JAVA Faker library to generate Test Data)

###Utilities
All Utilities Required For API Verification, Extent Report, JSON Processing are present under (src/test/java/utilities)

**Important Classes**

**APIBase**: This is the Base class where all @BeforeSuite and @AfterMethod operations are present.
This class controls how API is executed with Logging and validating json response with json Schema

**Extentmanager**: This class helps in generating and attaching the Extent Report

**TestListeners**: This class implements ITestListener and logs the Test case execution either pass or fail

**TestUtil**: This class helps in logging for Logs and Extent Report by Logging API response, Headers

## How to Run Test cases:

Go To **TestNG.xml** file present under path( src/test/TestNG.xml)

**Steps To Run the Test cases and generate Execution Report:**

Go To the Project location under command line or terminal:

**Enter command:**
 mvn clean test

> Wait for Tests to Execute till you  see something like
> Results:
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 30.337 sec - in TestSuite
Results :
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0

***Now Test cases have run successfully, Test Reports and Test Logs must have been generated***

**LOGS:** Test Case Execution logs should be present under (log/testlog.log)

**Test case Execution Report**: Test case Execution report should be present under (src/main/testReport/TestReport.html)

