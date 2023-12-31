package com.example.restBookerApi.steps;

import com.example.restBookerApi.utis.authentication.AuthenticationRequest;
import com.example.restBookerApi.utis.requestbodies.BookingRequest;
import io.restassured.response.Response;

public class ApiRequests {

    public static Response getAuthenticationToken(AuthenticationRequest request) {
        return ApiConfig.sendRequest("POST", "/auth", request.requestBody.toString(), false);
    }

    public static Response getBooking(String firstname, String lastname) {
        String queryParameters = "?firstname=" + firstname + "&lastname=" + lastname;
        return ApiConfig.sendRequest("GET", "/booking" + queryParameters, null, true);
    }

    public static Response getBookingById(String bookingId) {
        return ApiConfig.sendRequest("GET", "/booking/", bookingId, null, true);
    }

    public static Response createBooking(BookingRequest request) {
        return ApiConfig.sendRequest("POST", "/booking", request.requestBody.toString(), true);
    }

    public static Response updateBooking(String bookingId, BookingRequest request) {
        return ApiConfig.sendRequest("PUT", "/booking/", bookingId, request.requestBody.toString(), true);
    }

    public static Response updateBookingNoAuth(String bookingId, BookingRequest request) {
        return ApiConfig.sendRequest("PUT", "/booking/", bookingId, request.requestBody.toString(), false);
    }

    public static Response partialUpdateBooking(String bookingId, BookingRequest request) {
        return ApiConfig.sendRequest("PATCH", "/booking/", bookingId, request.requestBody.toString(), true);
    }

    public static Response partialUpdateBookingNoAuth(String bookingId, BookingRequest request) {
        return ApiConfig.sendRequest("PATCH", "/booking/", bookingId, request.requestBody.toString(), false);
    }

    public static Response deleteBooking(String bookingId) {
        return ApiConfig.sendRequest("DELETE", "/booking/", bookingId, null, true);
    }

    public static Response deleteBookingNoAuth(String bookingId) {
        return ApiConfig.sendRequest("DELETE", "/booking/", bookingId, null, false);
    }
}
