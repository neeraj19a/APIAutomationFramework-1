package testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import pojo.BookingIdsResponse;
import pojo.Bookingdates;
import pojo.Bookingdetails;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static utilities.FakerUtils.*;

public class TestCaseUtil {

    public Bookingdetails createNewBooking() {
        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin(checkinDate());
        bookingdates.setCheckout(checkoutDate());

        Bookingdetails bookingdetails = bookingdetailsBuilder(generateFirstName(), generateLastName(), price(), false, bookingdates, additionalneeds());

        return bookingdetails;
    }

    public Bookingdetails patchUpdateBooking() {

        Bookingdetails bookingdetails = Bookingdetails.builder().firstname(generateFirstName()).lastname(generateLastName()).totalprice(price()).build();

        return bookingdetails;
    }
    public Bookingdetails bookingdetailsBuilder(String firstname, String lastname, int totalprice, boolean depositpaid, Bookingdates bookingdates, String additionalneeds) {
        return Bookingdetails.builder().
                firstname(firstname).
                lastname(lastname).
                totalprice(totalprice).
                depositpaid(depositpaid).
                bookingdates(bookingdates).
                additionalneeds(additionalneeds).
                build();
    }

    public static void assertBookingDetailsEqual(Bookingdetails response, Bookingdetails request) {
        assertThat(response.getFirstname(), equalTo(request.getFirstname()));
        assertThat(response.getLastname(), equalTo(request.getLastname()));
        assertThat(response.getTotalprice(), equalTo(request.getTotalprice()));
        assertThat(response.getAdditionalneeds(), equalTo(request.getAdditionalneeds()));

    }

    public static void assertBookingIdsResponse(List<BookingIdsResponse> response) {
        for (BookingIdsResponse bookingIdsResponse : response) {
            assertThat(bookingIdsResponse.getBookingid(), notNullValue());
        }
    }

    public List<BookingIdsResponse> returnBookingIdsResponseList(Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<BookingIdsResponse> bookingIdsResponseList = Arrays.asList(mapper.readValue(response.asString(), BookingIdsResponse[].class));
        return bookingIdsResponseList;
    }

    public void assertBookingdetails(Bookingdetails response, Bookingdetails request) throws JsonProcessingException {
        assertThat(response.getFirstname(), equalTo(request.getFirstname()));
        assertThat(response.getLastname(), equalTo(request.getLastname()));
        assertThat(response.getTotalprice(), equalTo(request.getTotalprice()));
        assertThat(response.getAdditionalneeds(), equalTo(request.getAdditionalneeds()));
    }

    public void assertPatchBookingdetails(Bookingdetails response, Bookingdetails patchrequest,Bookingdetails putrequest) throws JsonProcessingException {
        assertThat(response.getFirstname(), equalTo(patchrequest.getFirstname()));
        assertThat(response.getLastname(), equalTo(patchrequest.getLastname()));
        assertThat(response.getTotalprice(),equalTo(patchrequest.getTotalprice()));
        assertThat(response.getAdditionalneeds(),equalTo(putrequest.getAdditionalneeds()));
        assertThat(response.getBookingdates().getCheckout(),equalTo(putrequest.getBookingdates().getCheckout()));
        assertThat(response.getBookingdates().getCheckin(),equalTo(putrequest.getBookingdates().getCheckin()));

    }
    public void assertBookingdetailsNotNull(Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        List<Bookingdetails> bookingdetails = Arrays.asList(mapper.readValue(response.getBody().asString(), Bookingdetails[].class));

        assertThat(bookingdetails.get(0).getFirstname(), notNullValue());
        assertThat(bookingdetails.get(0).getLastname(), notNullValue());
        assertThat(bookingdetails.get(0).getTotalprice(), notNullValue());
    }

}
