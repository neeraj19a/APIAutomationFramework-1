package testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import pojo.AuthRequest;
import pojo.BookingIdsResponse;
import pojo.Bookingdetails;
import pojo.StatusCode;
import utilities.APIBase;
import utilities.APIConstant;

import java.util.List;

import static config.ConfigurationManager.configuration;
public class TestCasesExistingBooking extends APIBase {
    public static Response response;
    public static Logger log = Logger.getLogger(TestCasesExistingBooking.class);
    private static int bookingId;
    public List<BookingIdsResponse> bookingIdsResponseList;
    public Bookingdetails bookingDetailsUpdateRequest;

    @Test(priority = 1)
    public void getListOfBooking() throws JsonProcessingException {
        TestCaseUtil testCaseUtil=new TestCaseUtil();

        //Executing API
        response = new APIBase(APIConstant.APIPaths.BOOKING, APIConstant.ApiMethods.GET).execute();

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);

        //Json Schema Validator
        jsonSchemaValidator(response, configuration().getBookingIdSchema());
        bookingIdsResponseList=testCaseUtil.returnBookingIdsResponseList(response);
        testCaseUtil.assertBookingIdsResponse(bookingIdsResponseList);
    }


    @Test(priority = 2)
    public void getExistingBookingById() throws JsonProcessingException {
        TestCaseUtil testCaseUtil = new TestCaseUtil();
        //Lets Try FirstBooking Id
        bookingId= bookingIdsResponseList.get(0).getBookingid();
        log.info("Fetching Details for Existing BookingId --->" + bookingId);
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" +bookingId, APIConstant.ApiMethods.GET).execute();

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);

        //Json Schema Validator
        jsonSchemaValidator(response, configuration().getBookingSchema());
        testCaseUtil.assertBookingdetailsNotNull(response);
    }

    @Test(priority = 3)
    public void updateExistingBookingWithoutToken()  {

        TestCaseUtil testCaseUtil = new TestCaseUtil();
        bookingDetailsUpdateRequest = testCaseUtil.createNewBooking();
        log.info("Updating BookingId --->" + bookingId+"with bookingDetailsRequest-->"+bookingDetailsUpdateRequest);
        Gson gson=new Gson();
        String body=gson.toJson(bookingDetailsUpdateRequest);
        //Executing API
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.PUT).body(body);

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_403);
      }

    @Test(priority = 4)
    public void patchExistingBookingWithoutToken()  {
        log.info("PATCH Existing BookingId --->" + bookingId);

        TestCaseUtil testCaseUtil = new TestCaseUtil();
        Bookingdetails patchBookingDetails=testCaseUtil.patchUpdateBooking();
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.PATCH).body(patchBookingDetails);

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_403);
    }

    @Test(priority = 5)
    public void generateToken() {

        AuthRequest authRequest = AuthRequest.builder().username(configuration().username()).password(configuration().password()).build();
        Response response = new APIBase(APIConstant.APIPaths.AUTH, APIConstant.ApiMethods.POST).body(authRequest);

        token = response.getBody().jsonPath().get("token");
        log.info("Token created From AUTH API is --->" + token);

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);

    }

    @Test(priority = 6)
    public void updateExistingBooking()  {

        TestCaseUtil testCaseUtil = new TestCaseUtil();
        bookingDetailsUpdateRequest = testCaseUtil.createNewBooking();
        log.info("Updating BookingId --->" + bookingId+"with bookingDetailsRequest-->"+bookingDetailsUpdateRequest);
        Gson gson=new Gson();
        String body=gson.toJson(bookingDetailsUpdateRequest);
        //Executing API
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.PUT).body(body);

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);
        jsonSchemaValidator(response, configuration().getBookingSchema());
        testCaseUtil.assertBookingDetailsEqual(response.getBody().as(Bookingdetails.class), bookingDetailsUpdateRequest);
    }

    @Test(priority = 7)
    public void getLastUpdatedExistingBooking() throws JsonProcessingException {

        TestCaseUtil testCaseUtil = new TestCaseUtil();
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.GET).execute();

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_200);

        //Json Schema Validator
        jsonSchemaValidator(response, configuration().getBookingSchema());
        testCaseUtil.assertBookingdetails(response.as(Bookingdetails.class), bookingDetailsUpdateRequest);
    }


    @Test(priority = 8)
    public void patchLastUpdatedExistingBooking() throws JsonProcessingException {

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

    @Test(priority = 9)
    public void deleteExistingBooking()  {
        log.info("Deleting Existing BookingId --->" + bookingId);
        //Executing API
        response = new APIBase(APIConstant.APIPaths.BOOKING + "/" + bookingId, APIConstant.ApiMethods.DELETE).execute();

        //Validating Response Code
        responsecodeValidation(response, StatusCode.CODE_201);
    }


}
