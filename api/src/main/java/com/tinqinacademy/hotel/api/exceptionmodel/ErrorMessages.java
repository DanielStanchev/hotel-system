package com.tinqinacademy.hotel.api.exceptionmodel;

public class ErrorMessages {
    public static final String BED_NOT_FOUND = "No Bed found";
    public static final String ROOM_NOT_FOUND = "No Room found";
    public static final String USER_NOT_FOUND = "No User found";
    public static final String BOOKING_NOT_FOUND = "No Booking found";
    public static final String COUNT_OF_BEDS_NOT_EQUAL_BEDS_ADDED = "The number of beds to be saved does not correspond to the beds added to the room.";
    public static final String UNDERAGE_USER = "User must be at least 18 years old to register";
    public static final String ROOM_ALREADY_BOOKED = "Room has future bookings and cannot be deleted";
    public static final String BOOKING_ALREADY_HAVE_GUESTS_REGISTERED = "Booking cannot be deleted because have already registered guests.";
}

