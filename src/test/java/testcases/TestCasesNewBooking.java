package testcases;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import pojo.*;
import utilities.APIBase;
import utilities.APIConstant;

import java.util.List;

import static config.ConfigurationManager.configuration;
public class TestCasesNewBooking extends APIBase {
    public static Response response;
    public static Logger log = Logger.getLogger(TestCasesNewBooking.class);
    private static int bookingId;
    public Bookingdetails bookingDetailsRequest;
    public Bookingdetails bookingDetailsUpdateRequest;

    @Test(priority = 1)
    public void getListOfBooking() throws JsonProcessingException {
        TestCaseUtil testCaseUtil = new TestCaseUtil();

        //Executing API
        response = new APIBase(APIConstant.APIPaths.BOOKING, APIConstant.ApiMethods.GET).execute();

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);

        //Json Schema Validator
        jsonSchemaValidator(response, configuration().getBookingIdSchema());
        List<BookingIdsResponse> bookingIdsResponseList = testCaseUtil.returnBookingIdsResponseList(response);
        testCaseUtil.assertBookingIdsResponse(bookingIdsResponseList);
    }


    @Test(priority = 2)
    public void createBooking() {
        //Creating Sample Data (New Booking ) with Builder
        TestCaseUtil testCaseUtil = new TestCaseUtil();
        bookingDetailsRequest = testCaseUtil.createNewBooking();

        //Executing API
        response = new APIBase(APIConstant.APIPaths.BOOKING, APIConstant.ApiMethods.POST).body(bookingDetailsRequest);

        bookingId = response.getBody().jsonPath().get("bookingid");
        log.info("Booking id created is--->" + bookingId);

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);
        //Validating Schema
        jsonSchemaValidator(response, configuration().bookingResponseSchema());
        //Validating Actual and Expected Response
        Bookingdetails actualBookingdetails = response.as(BookingResponse.class).getBooking();
        testCaseUtil.assertBookingDetailsEqual(actualBookingdetails, bookingDetailsRequest);

    }

    @Test(priority = 3)
    public void getLastCreatedBookingById() throws JsonProcessingException {
        TestCaseUtil testCaseUtil = new TestCaseUtil();
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.GET).execute();

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);

        //Json Schema Validator
        jsonSchemaValidator(response, configuration().getBookingSchema());
        testCaseUtil.assertBookingdetails(response.as(Bookingdetails.class), bookingDetailsRequest);
    }

    @Test(priority = 4)
    public void generateToken() {

        AuthRequest authRequest = AuthRequest.builder().username(configuration().username()).password(configuration().password()).build();
        Response response = new APIBase(APIConstant.APIPaths.AUTH, APIConstant.ApiMethods.POST).body(authRequest);

        token = response.getBody().jsonPath().get("token");
        log.info("Token created From AUTH API is --->" + token);

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);

    }


    @Test(priority = 5)
    public void updateLastCreatedBooking() {

        TestCaseUtil testCaseUtil = new TestCaseUtil();
        bookingDetailsUpdateRequest = testCaseUtil.createNewBooking();

        //Executing API
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.PUT).body(bookingDetailsUpdateRequest);

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);
        jsonSchemaValidator(response, configuration().getBookingSchema());
        testCaseUtil.assertBookingDetailsEqual(response.getBody().as(Bookingdetails.class), bookingDetailsUpdateRequest);
    }

    @Test(priority = 6)
    public void getLastUpdatedBooking() throws JsonProcessingException {

        TestCaseUtil testCaseUtil = new TestCaseUtil();
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.GET).execute();

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);

        //Json Schema Validator
        jsonSchemaValidator(response, configuration().getBookingSchema());
        testCaseUtil.assertBookingdetails(response.as(Bookingdetails.class), bookingDetailsUpdateRequest);
    }


    @Test(priority = 7)
    public void patchLastUpdatedBooking() throws JsonProcessingException {

        TestCaseUtil testCaseUtil = new TestCaseUtil();
        Bookingdetails patchBookingDetails=testCaseUtil.patchUpdateBooking();
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.PATCH).body(patchBookingDetails);

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);
       log.info("PATCH Response-->"+response.getBody().asString());
        //Json Schema Validator
        jsonSchemaValidator(response, configuration().getBookingSchema());
        testCaseUtil.assertPatchBookingdetails(response.getBody().as(Bookingdetails.class), patchBookingDetails,bookingDetailsUpdateRequest);
    }


    @Test(priority = 8)
    public void deleteLastCreatedBooking() {

        //Executing API
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.DELETE).execute();

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_201);
    }

    @Test(priority = 9)
    public void getLastDeletedBooking() {

        //Executing API
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.GET).execute();

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_404);
    }


}
