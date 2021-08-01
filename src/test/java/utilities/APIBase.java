package utilities;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pojo.StatusCode;


import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static config.ConfigurationManager.configuration;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

public class APIBase {
    //FOR LOGS
    public static Logger log = Logger.getLogger(APIBase.class);

    //For Report generation
    public static ExtentReports extentReport;
    public static ThreadLocal<ExtentTest> classLevelLog = new ThreadLocal<ExtentTest>(); //LHS Node create
    public static ThreadLocal<ExtentTest> testLevelLog = new ThreadLocal<ExtentTest>();   //Test case Log
    static StringWriter requestWriter;
    static PrintStream requestCapture;
    private RequestSpecBuilder builder = new RequestSpecBuilder();
    //Token will be generated and used for PATCH AND DELETE Methods
    public  static String  token="";
    private String method;
    private String url;

    public APIBase() {

    }

    /**
     * APIBaseLatest constructor With Token to pass the initial settings for the the following method
     *
     * @param uri
     * @param method
     * @param token
     */
    public APIBase(String uri, String method, String token) {

        //Formulate the API url
        this.url = configuration().url() + uri;
        log.info("URL-->" + this.url);

        this.method = method;

        if (token != null)
            builder.addHeader("Authorization", "Bearer " + token);
    }

    /**
     * APIBaseLatest constructor Without Token to pass the initial settings for the the following method
     *
     * @param uri
     * @param method
     */
    public APIBase(String uri, String method) {
        //Formulate the API url
        this.url = configuration().url() + uri;
        log.info("URL-->" + this.url);

        this.method = method;

    }


    @BeforeSuite
    public void setUp() {
        PropertyConfigurator.configure(configuration().getlog4jproperties());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        //call getExtent method present in Extentmanager class and send path of report to be generated
        // Extent Report: Create object for Extent Manager class and in initialize
        // report -Name...Location..
        extentReport = ExtentManager
                .GetExtent(configuration().getExtentReport());
        extentReport.setSystemInfo("OS", "Windows 10");
        extentReport.setSystemInfo("Host Name", "T460");
        extentReport.setSystemInfo("Environment", "Neeraj Bakhtani");
        extentReport.setSystemInfo("Report Name", "REST API Report");
    }

    //Create node in LHS class level in extent report
    @BeforeClass
    public synchronized void beforeClass() {

        //create class at lhs of the report for each class
        ExtentTest test = extentReport.createTest(getClass().getSimpleName());
        classLevelLog.set(test);
    }

    /**
     * Execute Function to execute the API for GET/POST/DELETE/PUT/PATCH
     *
     * @return Response
     */
    public Response execute() {
        RequestSpecification requestSpecification = builder.build();

        RequestSpecification request = RestAssured.given().log().all();
        request.contentType(ContentType.JSON);
        request.spec(requestSpecification);

        Response responseResponseOptions;

        requestWriter = new StringWriter();
        requestCapture = new PrintStream(new WriterOutputStream(requestWriter));

        if (this.method.equalsIgnoreCase(APIConstant.ApiMethods.POST)) {
            responseResponseOptions = request.filter(new RequestLoggingFilter(requestCapture)).post(this.url);
            requestCapture.flush();

            testLogging(responseResponseOptions, APIConstant.ApiMethods.POST);

            return responseResponseOptions;
        } else if (this.method.equalsIgnoreCase(APIConstant.ApiMethods.DELETE)) {
            responseResponseOptions = request.filter(new RequestLoggingFilter(requestCapture)).headers("Cookie", " token= "+token).delete(this.url);
            requestCapture.flush();

            testLogging(responseResponseOptions, APIConstant.ApiMethods.DELETE);
            return responseResponseOptions;

        } else if (this.method.equalsIgnoreCase(APIConstant.ApiMethods.GET)) {
            responseResponseOptions = request.filter(new RequestLoggingFilter(requestCapture)).get(this.url);
            requestCapture.flush();

            testLogging(responseResponseOptions, APIConstant.ApiMethods.GET);
            return responseResponseOptions;

        } else if (this.method.equalsIgnoreCase(APIConstant.ApiMethods.PUT)) {
            responseResponseOptions = request.filter(new RequestLoggingFilter(requestCapture)).headers("Cookie", " token= "+token).put(this.url);
            requestCapture.flush();

            testLogging(responseResponseOptions, APIConstant.ApiMethods.PUT);
            return responseResponseOptions;
        } else if (this.method.equalsIgnoreCase(APIConstant.ApiMethods.PATCH)) {
            responseResponseOptions = request.filter(new RequestLoggingFilter(requestCapture)).contentType("application/json").headers("Cookie", " token= "+token).patch(this.url);
            requestCapture.flush();

            testLogging(responseResponseOptions, APIConstant.ApiMethods.PATCH);
            return responseResponseOptions;
        }

        return null;
    }

    /**
     * Authenticate to get the token variable
     *
     * @param body
     * @return string token
     */
    //Object can be anything a HashMap or POJO
    public String Authenticate(Object body) {
        builder.setBody(body);
        return execute().getBody().jsonPath().get("access_token");
    }

    public Response body(Object body) {

        builder.setBody(body);
        return execute();
    }

    @BeforeMethod
    public synchronized void beforeMethod(Method method) {
        //log info in report
        ExtentTest child = classLevelLog.get().createNode(method.getName());
        testLevelLog.set(child);
        log.info("==================================================================================" + " \n");
        log.info("Execution of Test Case:- " + method.getName() + " started");
        testLevelLog.get().log(Status.INFO, " Execution of Test Case:- " + method.getName() + " started");
    }

    @AfterMethod
    protected void afterMethod(ITestResult result) {

        log.info("Execution of Test Case:- " + result.getName() + " finished" + "\n");
        log.info("==================================================================================" + " \n");
        testLevelLog.get().log(Status.INFO, "<b>" + "Execution of Test Case:- " + result.getName() + " finished" + "<b>");
    }

    @AfterSuite
    public void tearDownFramework() {
        extentReport.flush();
    }

    /**
     * Function to Log Time taken by The API to execute
     *
     * @param response
     * @return
     */
    public static Long getResponseTime(ResponseOptions<Response> response) {
        long time = response.timeIn(TimeUnit.MILLISECONDS);
        return time;
    }

    /**
     * Function to Validate JsonSchema
     *
     * @param response
     * @param json
     */
    public void jsonSchemaValidator(Response response, String json) {
        //Json Schema Validator
        try {
            assertThat(response.getBody().asString(), matchesJsonSchemaInClasspath(json));
            testLevelLog.get().pass("JSON Schema of " + json + " is valid ");
        } catch (AssertionError e) {
            testLevelLog.get().fail("<details>" + "<summary>" + "<b>" + "<font color=" + "red>" + "JSON Schema of " + json + " is not valid ,Pls Check " + "</font>" + "</b >" + "</summary>" + e.fillInStackTrace() + "</details>");
        } catch (Exception e) {
            testLevelLog.get().fail("Exception occurred " + e.fillInStackTrace());
        }
    }

    /**
     * Response Code Validation
     *
     * @param response
     * @param statusCode
     */
    public static void responsecodeValidation(ResponseOptions<Response> response, StatusCode statusCode) {
        try {
            Assert.assertEquals(statusCode.code, response.getStatusCode());
            testLevelLog.get().pass("PASS Successfully Validated the Status Code  " + response.getStatusCode());
        } catch (AssertionError error) {
            testLevelLog.get().fail("<summary>" + "<b>" + "<font color=" + "red>" + "FAIL Expected Status Code is :: " + statusCode + " instead of getting :: " + response.getStatusCode() + "</font>" + "</b >" + "</summary>");
        } catch (Exception e) {
            testLevelLog.get().fail("FAIL" + e.fillInStackTrace());
        }
    }

    public void assertContent(Object postModel, Response response) {
        Map<?, ?> actualResponseBody = response.jsonPath().get();
        log.info("Actual Response Content:" + actualResponseBody);

        Map<?, ?> expectedResponseBody = ConvertModelToMap(postModel);
        log.info("Expected Response Content:" + expectedResponseBody);

        try {
            Assert.assertEquals(expectedResponseBody, actualResponseBody);
            log.info("PASS Validated Expected and Actual Response");
            testLevelLog.get().pass("PASS Validated Expected and Actual Response");
        } catch (AssertionError assertionError) {
            testLevelLog.get().fail("<details>" + "<summary>" + "<b>" + "<font color=" + "red>" + "Mismatch In Expected and Actual Response </font>" + "</b >" + "</summary>" + "Expected is \n " + expectedResponseBody + " \n and Actual Body is \n " + actualResponseBody + "</details>");
        } catch (Exception e) {
            testLevelLog.get().fail("FAIL" + e.fillInStackTrace());
        }
    }

    public String ConvertModelToJSON(Object model) {
        ObjectMapper objectMapper = new ObjectMapper();
        String postModelAsString = null;
        try {
            postModelAsString = objectMapper.writeValueAsString(model);
            return postModelAsString;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return postModelAsString;
    }

    public Map<?, ?> ConvertModelToMap(Object model) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> mappedObject = objectMapper.convertValue(model, Map.class);
        return mappedObject;
    }

    //-------FUNCTIONS FOR REPORTING and LOGGING--------------------------------//
    public static void testLogging(ResponseOptions<Response> responseResponseOptions, String method) {
        logInReport(responseResponseOptions.getHeaders());
        logInReport(requestWriter.toString(), method);
        logInReport(responseResponseOptions.getBody().asString());
        logTimeInReport(getResponseTime(responseResponseOptions));
    }

    public static void logInReport(Object object) {
        if (object instanceof RestAssuredResponseImpl) {
            Response obj = (Response) object;

            log.info("API Response" + " \n" + obj.getBody().asString());

            testLevelLog.get().info("<details>" + "<summary>" + "<b>" + "<font color=" + "green>" + "API Response"
                    + "</font>" + "</b >" + "</summary>" + obj.getBody().asString() + "</details>"
                    + " \n");
        } else if (object instanceof Headers) {
            log.info("Headers are here" + " \n" + Headers.class.cast(object) + "\n" + "\n");

            testLevelLog.get().info("<details>" + "<summary>" + "<b>" + "<font color=" + "green>" + "Headers are here"
                    + "</font>" + "</b >" + "</summary>" + Headers.class.cast(object) + "</details>"
                    + " \n");
        }

    }

    public static void logInReport(String string) {
        if (string.contains("Error")) {

            log.error("Errors are here" + " \n" + string);
            testLevelLog.get().fail("<details>" + "<summary>" + "<b>" + "<font color=" + "red>" + "Error Occurred "
                    + "</font>" + "</b >" + "</summary>" + string + "</details>"
                    + " \n");
        } else {
            log.info("Response is Here" + " \n" + string);
            testLevelLog.get().info("<details>" + "<summary>" + "<b>" + "<font color=" + "green>" + " Response is Here "
                    + "</font>" + "</b >" + "</summary>" + string + "</details>"
                    + " \n");
        }
    }

    public static void logInReport(Object object, String method) {
        if (object.toString().contains(method)) {
            log.info("Request is Here" + " \n" + String.class.cast(object));
            testLevelLog.get().info("<details>" + "<summary>" + "<b>" + "<font color=" + "green>" + method + " Request is Here "
                    + "</font>" + "</b >" + "</summary>" + String.class.cast(object) + "</details>"
                    + " \n");
        }
    }

    public static void logTimeInReport(Long time) {

        log.info("API Response Time is " + time);
        testLevelLog.get().info("<summary>" + "<b>" + "<font color=" + "green>" + " API Response Time is  " + time +
                "</font>" + "</b >" + "</summary>"
                + " \n");

    }

    /**
     * @param t
     * @return
     */
    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

}
