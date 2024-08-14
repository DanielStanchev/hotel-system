package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.getavailablerooms.GetAvailableRooms;
import com.tinqinacademy.hotel.api.operations.getavailablerooms.GetAvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.getavailablerooms.GetAvailableRoomsOutput;
import com.tinqinacademy.hotel.core.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.exception.ErrorMapper;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class GetAvailableRoomsOperationProcessor extends BaseOperationProcessor implements GetAvailableRooms {

    private final RoomRepository roomRepository;

    public GetAvailableRoomsOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper, RoomRepository roomRepository) {
        super(validator,conversionService,errorMapper);
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<ErrorWrapper,GetAvailableRoomsOutput> process(GetAvailableRoomsInput input) {
        log.info("Start checkAvailableRooms input: {} ",input);
        return getAvailableRooms(input);
    }

    private Either<ErrorWrapper, GetAvailableRoomsOutput> getAvailableRooms(GetAvailableRoomsInput input) {
        return Try.of(()->{

        List<BedSize> bedSizes = getBedSizes(input);
        List<Room> availableRooms = getAvailableRoomsByInputCriteria(input, bedSizes);
        List<String> roomsToString = mapRoomsToStringValues(availableRooms);
        GetAvailableRoomsOutput result = getConvertedRoomsToOutput(availableRooms, roomsToString);

        log.info("End checkAvailableRooms output: {} ", result);
        return result;

        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private GetAvailableRoomsOutput getConvertedRoomsToOutput(List<Room> availableRooms, List<String> roomsToString) {
        return conversionService.convert(availableRooms, GetAvailableRoomsOutput.GetAvailableRoomsOutputBuilder.class)
            .roomIds(roomsToString)
            .build();
    }

    private static List<String> mapRoomsToStringValues(List<Room> availableRooms) {
        return availableRooms.stream()
            .map(room -> room.getId().toString())
            .toList();
    }

    private List<Room> getAvailableRoomsByInputCriteria(GetAvailableRoomsInput input, List<BedSize> bedSizes) {
       List<Room> availableRooms = roomRepository.findRoomByCriteria(input.getStartDate(),
                                                                      input.getEndDate(),
                                                                      input.getBedCount(), bedSizes,
                                                                      BathroomType.getByCode(input.getBathroomType()));
        if (availableRooms.isEmpty()) {
            log.info("No available rooms found for the given criteria.");
            throw new NotFoundException(ErrorMessages.NO_ROOMS_AVAILABLE);
        }
        return availableRooms;
    }

    private static List<BedSize> getBedSizes(GetAvailableRoomsInput input) {
        return input.getBeds().stream()
            .map(BedSize::getByCode)
            .toList();
    }
}
