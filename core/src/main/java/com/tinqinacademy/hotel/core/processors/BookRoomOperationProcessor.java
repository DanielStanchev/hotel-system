package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoom;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.core.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.exception.ErrorMapper;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.entity.Booking;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;


@Slf4j
@Service
public class BookRoomOperationProcessor extends BaseOperationProcessor implements BookRoom {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public BookRoomOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper,
                                      RoomRepository roomRepository, BookingRepository bookingRepository) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Either<ErrorWrapper, BookRoomOutput> process(BookRoomInput input) {
        log.info("Start bookRoom input:{}.", input);
        return validateInput(input).flatMap(validated -> bookRoom(input));
    }

    private Either<ErrorWrapper, BookRoomOutput> bookRoom(BookRoomInput input) {
        return Try.of(() -> {
                checkBookingDatesValid(input);
                Room roomToBeBooked = getRoom(input);
                checkIfRoomHasAlreadyBookingForWantedDates(input, roomToBeBooked);
                Booking bookingToSave = getConvertedBooking(input, roomToBeBooked);
                bookingRepository.save(bookingToSave);
                BookRoomOutput result = BookRoomOutput.builder().build();
                log.info("End createRoom output:{}.", result);
                return result;

            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))));
    }

    private void checkIfRoomHasAlreadyBookingForWantedDates(BookRoomInput input, Room roomToBeBooked) {
        if (bookingRepository.checkRoomOccupied(roomToBeBooked.getId(), input.getEndDate(), input.getEndDate())) {
            throw new IllegalArgumentException("Room is occupied in the dates wanted)");
        }
    }

    private void checkBookingDatesValid(BookRoomInput input) {
        if(input.getStartDate().isAfter(input.getEndDate())) {
            throw new IllegalArgumentException("Start date must be after end date");
        }
        if(input.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date must be after today's date");
        }
    }

    private Booking getConvertedBooking(BookRoomInput input, Room roomToBeBooked) {
        return conversionService.convert(input, Booking.BookingBuilder.class)
            .roomBooked(roomToBeBooked)
            .build();
    }

    private Room getRoom(BookRoomInput input) {
        return roomRepository.findById(UUID.fromString(input.getRoomId()))
            .orElseThrow(() -> new NotFoundException(ErrorMessages.ROOM_NOT_FOUND));
    }
}

