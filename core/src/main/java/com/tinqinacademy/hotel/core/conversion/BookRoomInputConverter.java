package com.tinqinacademy.hotel.core.conversion;

import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.persistence.entity.Booking;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BookRoomInputConverter extends BaseConverter<BookRoomInput, Booking.BookingBuilder> {
    @Override
    public Booking.BookingBuilder convertObject(BookRoomInput input) {
        return Booking.builder()
            .startDate(input.getStartDate())
            .endDate(input.getEndDate())
            .userId(UUID.fromString(input.getUserId()));
    }
}