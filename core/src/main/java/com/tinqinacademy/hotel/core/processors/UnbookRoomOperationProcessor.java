package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoom;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.core.exception.ErrorMapper;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.entity.Booking;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class UnbookRoomOperationProcessor implements UnbookRoom {

    private final BookingRepository bookingRepository;
    private final ErrorMapper errorMapper;

    public UnbookRoomOperationProcessor(BookingRepository bookingRepository, ErrorMapper errorMapper) {
        this.bookingRepository = bookingRepository;
        this.errorMapper = errorMapper;
    }

    @Override
    public Either<ErrorWrapper,UnbookRoomOutput> process(UnbookRoomInput input) {
        log.info("Start unbook input:{}.", input);
        return unbookRoom(input);
    }

    private Either<ErrorWrapper, UnbookRoomOutput> unbookRoom(UnbookRoomInput input) {
        return Try.of(()->{

        Booking booking = getBooking(input);
        bookingRepository.delete(booking);
        UnbookRoomOutput result = UnbookRoomOutput.builder().build();

        log.info("End unbook output:{}.", result);
        return result;
        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
            Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private Booking getBooking(UnbookRoomInput input) {
        Booking booking = bookingRepository.findById(UUID.fromString(input.getId()))
            .orElseThrow(()->new NotFoundException(ErrorMessages.BOOKING_NOT_FOUND));

        if (!booking.getGuests().isEmpty()) {
            log.error("Booking cannot be deleted because have already registered guests.");
            throw new IllegalArgumentException(ErrorMessages.BOOKING_ALREADY_HAVE_GUESTS_REGISTERED);
        }
        return booking;
    }
}
