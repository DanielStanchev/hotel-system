package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoom;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.core.exception.ErrorMapper;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.entity.Booking;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class DeleteRoomOperationProcessor implements DeleteRoom {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ErrorMapper errorMapper;

    public DeleteRoomOperationProcessor(RoomRepository roomRepository, BookingRepository bookingRepository, ErrorMapper errorMapper) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.errorMapper = errorMapper;
    }

    @Override
    public Either<ErrorWrapper,DeleteRoomOutput> process(DeleteRoomInput input) {
        log.info("Start deleteRoom input: {} ",input);
        return deleteRoom(input);
    }

    private Either<ErrorWrapper, DeleteRoomOutput> deleteRoom(DeleteRoomInput input) {
        return Try.of(()->{

        Room rooToBeDeleted = getRoom(input);
        checkIfRoomHasBookingsInTheFuture(rooToBeDeleted);
        roomRepository.delete(rooToBeDeleted);
        DeleteRoomOutput result = DeleteRoomOutput.builder().build();

            log.info("End deleteRoom output {} deleted",result);
            return result;

        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
            Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private void checkIfRoomHasBookingsInTheFuture(Room rooToBeDeleted) {
        LocalDate currentDate = LocalDate.now();
        List<Booking> bookedRoomForTheFuture = bookingRepository.findBookingByRoomId(rooToBeDeleted.getId(), currentDate);

        if(!bookedRoomForTheFuture.isEmpty()){
            throw new IllegalArgumentException(ErrorMessages.ROOM_ALREADY_BOOKED);
        }
    }

    private Room getRoom(DeleteRoomInput input) {
        return roomRepository.findById(UUID.fromString(input.getId()))
            .orElseThrow(() -> new NotFoundException(ErrorMessages.ROOM_NOT_FOUND));
    }
}
