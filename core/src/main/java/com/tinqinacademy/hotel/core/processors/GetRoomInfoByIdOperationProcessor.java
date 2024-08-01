package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.getroominfobyid.GetRoomDatesOccupiedInfo;
import com.tinqinacademy.hotel.api.operations.getroominfobyid.GetRoomInfoById;
import com.tinqinacademy.hotel.api.operations.getroominfobyid.GetRoomInfoByIdInput;
import com.tinqinacademy.hotel.api.operations.getroominfobyid.GetRoomInfoByIdOutput;
import com.tinqinacademy.hotel.core.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.exception.ErrorMapper;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.entity.Bed;
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

import java.util.List;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class GetRoomInfoByIdOperationProcessor extends BaseOperationProcessor implements GetRoomInfoById {

    private final RoomRepository roomRepository;;
    private final BookingRepository bookingRepository;

    public GetRoomInfoByIdOperationProcessor(RoomRepository roomRepository, BookingRepository bookingRepository,
    ConversionService conversionService, Validator validator, ErrorMapper errorMapper) {
        super(validator, conversionService,errorMapper);
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Either<ErrorWrapper,GetRoomInfoByIdOutput> process(GetRoomInfoByIdInput input) {
        log.info("Start getRoomInfoById input:{}.", input);
        return getRoomInfoById(input);
    }

    private Either<ErrorWrapper, GetRoomInfoByIdOutput> getRoomInfoById(GetRoomInfoByIdInput input) {
       return Try.of(()-> {
        Room room = getRoom(input.getRoomId());
        List<String> bedsToString = getRoomBedsAsString(room);
        List<Booking> bookings = getRoomBookings(room);
        List<GetRoomDatesOccupiedInfo> getRoomDatesOccupiedInfo = getRoomOccupiedDatesByBooking(bookings);
        GetRoomInfoByIdOutput result = getConvertedRoomInfoToOutput(room, getRoomDatesOccupiedInfo, bedsToString);

        log.info("End getRoomInfoById output:{}.", result);
        return result;

        }).toEither()
            .mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private GetRoomInfoByIdOutput getConvertedRoomInfoToOutput(Room room, List<GetRoomDatesOccupiedInfo> getRoomDatesOccupiedInfo,
                                                           List<String> bedsToString) {
        return conversionService.convert(room, GetRoomInfoByIdOutput.GetRoomInfoByIdOutputBuilder.class)
            .getRoomDatesOccupiedInfo(getRoomDatesOccupiedInfo)
            .beds(bedsToString).build();
    }

    private static List<GetRoomDatesOccupiedInfo> getRoomOccupiedDatesByBooking(List<Booking> bookings) {
       return bookings.stream().map(booking -> GetRoomDatesOccupiedInfo.builder()
            .startDate(booking.getStartDate().toString())
            .endDate(booking.getEndDate().toString())
            .build()).toList();
    }

    private List<Booking> getRoomBookings(Room room) {
       return bookingRepository.findBookingByRoomBookedId(room.getId());
    }

    private static List<String> getRoomBedsAsString(Room room) {
        return room.getBeds().stream()
            .map(Bed::toString)
            .toList();
    }

    private Room getRoom(String roomId) {
        return roomRepository.findById(UUID.fromString(roomId))
            .orElseThrow(()-> new NotFoundException(ErrorMessages.ROOM_NOT_FOUND));
    }
}
