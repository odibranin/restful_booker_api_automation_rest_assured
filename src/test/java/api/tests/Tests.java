package api.tests;

import api.steps.ApiRequests;
import api.utis.authentication.AuthenticationRequest;
import api.utis.requestbodies.BookingDates;
import api.utis.requestbodies.BookingRequest;
import api.utis.validation.ResponseValidation;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.logging.Logger;

public class Tests {
    private static final Logger LOGGER = Logger.getLogger("Restful-booker API Test");
    private Response response;
    private String bookingId;
    private ResponseValidation validation;

    @Parameters({"validUsername", "validPassword"})
    @BeforeClass(groups = "positive")
    public void generateAuthTokenValidCredentials(String validUsername, String validPassword) {
        AuthenticationRequest request = new AuthenticationRequest(validUsername, validPassword);
        response = ApiRequests.getAuthenticationToken(request);
        String authToken = response.jsonPath().getString("token");
        validation = new ResponseValidation();
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 200);
    }

    @Parameters({
            "createBookingFirstName",
            "createBookingLastName",
            "createBookingTotalPrice",
            "createBookingBookingPaid",
            "createBookingCheckIn",
            "createBookingCheckOut",
            "createBookingAdditionalNeeds"
    })
    @Test(priority = 1, groups = "positive")
    public void createBooking(String createBookingFirstName,
                              String createBookingLastName,
                              int createBookingTotalPrice,
                              boolean createBookingBookingPaid,
                              String createBookingCheckIn,
                              String createBookingCheckOut,
                              String createBookingAdditionalNeeds
    ) {
        BookingDates bookingDates = new BookingDates(createBookingCheckIn, createBookingCheckOut);
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(createBookingFirstName)
                .setLastname(createBookingLastName)
                .setTotalprice(createBookingTotalPrice)
                .setDepositpaid(createBookingBookingPaid)
                .setBookingdates(bookingDates)
                .setAdditionalNeeds(createBookingAdditionalNeeds)
                .build();

        response = ApiRequests.createBooking(request);
        JsonPath jsonPath = response.jsonPath();
        this.bookingId = jsonPath.getString("bookingid");

        LOGGER.info(" BOOKING ID: " + bookingId);

        validation = new ResponseValidation();
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 200);
    }

    @Test(priority = 2, groups = "positive")
    public void getBookingById() {
        response = ApiRequests.getBookingById(this.bookingId);

        // Log the response body to the console
        String responseBody = response.getBody().asString();
        LOGGER.info("RESPONSE BODY FOR BOOKING BY ID: " + responseBody);

        validation = new ResponseValidation();
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 200);
    }

    @Parameters({"createBookingFirstName", "createBookingLastName"})
    @Test(priority = 3, groups = "positive")
    public void getBooking(String createBookingFirstName, String createBookingLastName) {
        response = ApiRequests.getBooking(createBookingFirstName, createBookingLastName);


        String responseBody = response.getBody().asString();
        LOGGER.info("BOOKINGS THAT SHARE PROVIDED FIRSTNAME AND LASTNAME: " + responseBody);

        validation = new ResponseValidation();
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 200);
    }

    @Parameters({
            "updateBookingFirstName",
            "updateBookingLastName",
            "updateBookingTotalPrice",
            "updateBookingBookingPaid",
            "updateBookingCheckIn",
            "updateBookingCheckOut",
            "updateBookingAdditionalNeeds"
    })
    @Test(priority = 4, groups = "positive")
    public void updateBooking(String updateBookingFirstName,
                              String updateBookingLastName,
                              int updateBookingTotalPrice,
                              boolean updateBookingBookingPaid,
                              String updateBookingCheckIn,
                              String updateBookingCheckOut,
                              String updateBookingAdditionalNeeds
    ) {

        BookingDates bookingDates = new BookingDates(updateBookingCheckIn, updateBookingCheckOut);
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(updateBookingFirstName)
                .setLastname(updateBookingLastName)
                .setTotalprice(updateBookingTotalPrice)
                .setDepositpaid(updateBookingBookingPaid)
                .setBookingdates(bookingDates)
                .setAdditionalNeeds(updateBookingAdditionalNeeds)
                .build();

        response = ApiRequests.updateBooking(this.bookingId, request);
        validation = new ResponseValidation();
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 200);
    }

    @Parameters({
            "partialUpdateBookingFirstName",
            "partialUpdateBookingLastName"
    })
    @Test(priority = 5, groups = "positive")
    public void partialUpdateBooking(String partialUpdateBookingFirstName, String partialUpdateBookingLastName
    ) {
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(partialUpdateBookingFirstName)
                .setLastname(partialUpdateBookingLastName)
                .build();

        response = ApiRequests.partialUpdateBooking(this.bookingId, request);
        validation = new ResponseValidation();
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 200);
    }

    @Test(priority = 6, dependsOnMethods = "createBooking", groups = "positive")
    public void deleteBooking() {
        response = ApiRequests.deleteBooking(this.bookingId);
        validation = new ResponseValidation();
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 201);
    }

    // Edge cases for generating authentication token
    @Parameters({"invalidUsername", "validPassword"})
    @Test(groups = "negative")
    public void generateAuthTokenInvalidUsername(String invalidUsername, String validPassword) {
        AuthenticationRequest request = new AuthenticationRequest(invalidUsername, validPassword);
        response = ApiRequests.getAuthenticationToken(request);
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 400);
        validation.validateResponseErrorMessage(response);
    }

    @Parameters({"validUsername", "invalidPassword"})
    @Test(groups = "negative")
    public void generateAuthTokenInvalidPassword(String validUsername, String invalidPassword) {
        AuthenticationRequest request = new AuthenticationRequest(validUsername, invalidPassword);
        response = ApiRequests.getAuthenticationToken(request);

        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 400);
        validation.validateResponseErrorMessage(response);
    }

    @Parameters({"invalidUsernameIntDataType", "invalidPasswordIntDataType"})
    @Test(groups = "negative")
    public void generateAuthTokenInvalidCredentialDataType(String invalidUsernameIntDataType, String invalidPasswordIntDataType) {
        AuthenticationRequest request = new AuthenticationRequest(invalidUsernameIntDataType, invalidPasswordIntDataType);
        response = ApiRequests.getAuthenticationToken(request);
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 400);
        validation.validateResponseErrorMessage(response);
    }

    @Parameters({"emptyStringUserName", "emptyStringPassword"})
    @Test(groups = "negative")
    public void generateAuthTokenEmptyBody(String emptyStringUserName, String emptyStringPassword) {
        AuthenticationRequest request = new AuthenticationRequest(emptyStringUserName, emptyStringPassword);
        response = ApiRequests.getAuthenticationToken(request);
        validation.validateResponseHeaders(response);
        validation.validateResponseStatusCode(response, 400);
        validation.validateResponseErrorMessage(response);
    }

    //Edge cases for creating a booking
    @Test(groups = "negative")
    public void createBookingEmptyBody() {
        BookingRequest request = new BookingRequest.Builder()
                .build();
        response = ApiRequests.createBooking(request);
        validation.validateResponseStatusCode(response, 400);
    }

    @Parameters({
            "createBookingFirstName",
            "createBookingTotalPrice",
            "createBookingBookingPaid",
            "createBookingCheckIn",
            "createBookingCheckOut",
            "createBookingAdditionalNeeds"
    })
    @Test(groups = "negative")
    public void createBookingBodyPropertyMissing(String createBookingFirstName,
                                                 int createBookingTotalPrice,
                                                 boolean createBookingBookingPaid,
                                                 String createBookingCheckIn,
                                                 String createBookingCheckOut,
                                                 String createBookingAdditionalNeeds
    ) {

        BookingDates bookingDates = new BookingDates(createBookingCheckIn, createBookingCheckOut);
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(createBookingFirstName)
                .setTotalprice(createBookingTotalPrice)
                .setDepositpaid(createBookingBookingPaid)
                .setBookingdates(bookingDates)
                .setAdditionalNeeds(createBookingAdditionalNeeds)
                .build();

        response = ApiRequests.createBooking(request);
        validation.validateResponseStatusCode(response, 400);
    }

    @Parameters({
            "invalidUsernameIntDataType",
            "invalidPasswordIntDataType",
            "createBookingTotalPrice",
            "createBookingBookingPaid",
            "createBookingCheckIn",
            "createBookingCheckOut",
            "createBookingAdditionalNeeds"
    })
    @Test(groups = "negative")
    public void createBookingInvalidDataTypePropertyTest(int invalidUsernameIntDataType,
                                                         int invalidPasswordIntDataType,
                                                         int createBookingTotalPrice,
                                                         boolean createBookingBookingPaid,
                                                         String createBookingCheckIn,
                                                         String createBookingCheckOut,
                                                         String createBookingAdditionalNeeds
    ) {
        BookingDates bookingDates = new BookingDates(createBookingCheckIn, createBookingCheckOut);
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(invalidUsernameIntDataType)
                .setLastname(invalidPasswordIntDataType)
                .setTotalprice(createBookingTotalPrice)
                .setDepositpaid(createBookingBookingPaid)
                .setBookingdates(bookingDates)
                .setAdditionalNeeds(createBookingAdditionalNeeds)
                .build();

        response = ApiRequests.createBooking(request);
        validation.validateResponseStatusCode(response, 400);
    }

    @Parameters({
            "createBookingFirstName",
            "createBookingLastName",
            "createBookingTotalPrice",
            "createBookingBookingPaid",
            "createBookingCheckInInvalidFormat",
            "createBookingCheckOutInvalidFormat",
            "createBookingAdditionalNeeds"
    })
    @Test(groups = "negative")
    public void createBookingInvalidBookingDateFormat(String createBookingFirstName,
                                                      String createBookingLastName,
                                                      int createBookingTotalPrice,
                                                      boolean createBookingBookingPaid,
                                                      String createBookingCheckIn,
                                                      String createBookingCheckOut,
                                                      String createBookingAdditionalNeeds
    ) {
        BookingDates bookingDates = new BookingDates(createBookingCheckIn, createBookingCheckOut);
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(createBookingFirstName)
                .setLastname(createBookingLastName)
                .setTotalprice(createBookingTotalPrice)
                .setDepositpaid(createBookingBookingPaid)
                .setBookingdates(bookingDates)
                .setAdditionalNeeds(createBookingAdditionalNeeds)
                .build();

        response = ApiRequests.createBooking(request);
        validation.validateResponseStatusCode(response, 400);
        validation.validateDResponseDatePropertyFormat(response, "checkin");
        validation.validateDResponseDatePropertyFormat(response, "checkout");
    }

    // Edge cases for getting bookings ID
    @Test(groups = "negative")
    public void getBookingNonexistentID() {
        response = ApiRequests.getBookingById("");
        validation.validateResponseStatusCode(response, 404);
    }

    // Edge cases for getting bookings with invalid path parameters
    @Parameters({"createBookingFirstName", "createBookingInvalidLastName"})
    @Test(groups = "negative")
    public void getBookingIDWithWrongPathParam(String createBookingFirstName, String createBookingInvalidLastName) {
        response = ApiRequests.getBooking(createBookingFirstName, createBookingInvalidLastName);
        validation.validateResponseStatusCode(response, 200);
        validation.validateResponseBodyIsEmptyArray(response);
    }

    // Edge cases for updating a booking
    @Parameters({
            "updateBookingFirstName",
            "updateBookingLastName",
            "updateBookingTotalPrice",
            "updateBookingBookingPaid",
            "updateBookingCheckIn",
            "updateBookingCheckOut",
            "updateBookingAdditionalNeeds",
    })

    // Edge cases for updating a booking
    @Test(groups = "negative")
    public void updateBookingNonexistentID(String updateBookingFirstName,
                                           String updateBookingLastName,
                                           int updateBookingTotalPrice,
                                           boolean updateBookingBookingPaid,
                                           String updateBookingCheckIn,
                                           String updateBookingCheckOut,
                                           String updateBookingAdditionalNeeds
    ) {

        BookingDates bookingDates = new BookingDates(updateBookingCheckIn, updateBookingCheckOut);
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(updateBookingFirstName)
                .setLastname(updateBookingLastName)
                .setTotalprice(updateBookingTotalPrice)
                .setDepositpaid(updateBookingBookingPaid)
                .setBookingdates(bookingDates)
                .setAdditionalNeeds(updateBookingAdditionalNeeds)
                .build();

        response = ApiRequests.updateBooking("", request);
        validation.validateResponseStatusCode(response, 404);
    }

    @Parameters({
            "updateBookingFirstName",
            "updateBookingLastName",
            "updateBookingTotalPrice",
            "updateBookingBookingPaid",
            "updateBookingCheckIn",
            "updateBookingCheckOut",
            "updateBookingAdditionalNeeds",
    })
    @Test(groups = "negative")
    public void updateBookingNoAuthorization(String updateBookingFirstName,
                                             String updateBookingLastName,
                                             int updateBookingTotalPrice,
                                             boolean updateBookingBookingPaid,
                                             String updateBookingCheckIn,
                                             String updateBookingCheckOut,
                                             String updateBookingAdditionalNeeds
    ) {

        BookingDates bookingDates = new BookingDates(updateBookingCheckIn, updateBookingCheckOut);
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(updateBookingFirstName)
                .setLastname(updateBookingLastName)
                .setTotalprice(updateBookingTotalPrice)
                .setDepositpaid(updateBookingBookingPaid)
                .setBookingdates(bookingDates)
                .setAdditionalNeeds(updateBookingAdditionalNeeds)
                .build();

        response = ApiRequests.updateBookingNoAuth("", request);
        validation.validateResponseStatusCode(response, 403);
    }

    // Edge cases for partially updating a booking
    @Parameters({
            "partialUpdateBookingFirstName",
            "partialUpdateBookingLastName"
    })
    @Test(groups = "negative")
    public void partialUpdateBookingNonexistentID(String partialUpdateBookingFirstName,
                                                  String partialUpdateBookingLastName
    ) {
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(partialUpdateBookingFirstName)
                .setLastname(partialUpdateBookingLastName)
                .build();

        response = ApiRequests.partialUpdateBooking("", request);
        validation.validateResponseStatusCode(response, 404);
    }

    @Parameters({
            "partialUpdateBookingFirstName",
            "partialUpdateBookingLastName"
    })
    @Test(groups = "negative")
    public void partialUpdateBookingNoAuth(String partialUpdateBookingFirstName,
                                           String partialUpdateBookingLastName
    ) {
        BookingRequest request = new BookingRequest.Builder()
                .setFirstname(partialUpdateBookingFirstName)
                .setLastname(partialUpdateBookingLastName)
                .build();

        response = ApiRequests.partialUpdateBookingNoAuth("", request);
        validation.validateResponseStatusCode(response, 403);
    }

    // Edge cases for deleting a booking
    @Test(groups = "negative")
    public void deleteBookingNonExistingID() {
        response = ApiRequests.deleteBooking("someNonExistingID");
        validation.validateResponseStatusCode(response, 404);
    }

    @Test(groups = "negative")
    public void deleteBookingNoAuth() {
        response = ApiRequests.deleteBookingNoAuth("someBookingID");
        validation.validateResponseStatusCode(response, 403);
    }
}
