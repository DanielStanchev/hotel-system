package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoom;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.core.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.exception.ErrorMapper;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
public class UpdateRoomOperationProcessor extends BaseOperationProcessor implements UpdateRoom {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ModelMapper modelMapper;

    public UpdateRoomOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper,
                                        RoomRepository roomRepository, BedRepository bedRepository, ModelMapper modelMapper) {
        super(validator, conversionService,errorMapper);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Either<ErrorWrapper,UpdateRoomOutput> process (UpdateRoomInput input) {
        log.info("Start updateRoom input:{}.", input);
        return validateInput(input).flatMap(validated-> updateRoom(input));
    }

    private Either<ErrorWrapper, UpdateRoomOutput> updateRoom(UpdateRoomInput input) {
        return Try.of(()->{
        Room existingRoom = getRoom(input);
        List<Bed> bedsToUpdate = getBeds(input.getBeds());
        checkIfInputBedCountEqualsNumberOfBedsToUpdate(input, bedsToUpdate);
        Room roomToSave = getConvertedRoomByInput(input, bedsToUpdate, existingRoom);
        roomRepository.save(roomToSave);
        UpdateRoomOutput result = UpdateRoomOutput.builder().id(String.valueOf(existingRoom.getId())).build();

        log.info("End updateRoom output:{}.", result);
        return result;
        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
            Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private Room getConvertedRoomByInput(UpdateRoomInput input, List<Bed> bedsToUpdate, Room existingRoom) {
        Room roomToSave = conversionService.convert(input, Room.RoomBuilder.class)
            .beds(bedsToUpdate)
            .build();
        roomToSave.setId(existingRoom.getId());
        return roomToSave;
    }

    private static void checkIfInputBedCountEqualsNumberOfBedsToUpdate(UpdateRoomInput input, List<Bed> bedsToUpdate) {
        if (!input.getBedCount()
            .equals(bedsToUpdate.size())) {
            log.info("The number of beds to be updated do not correspond to the beds added to the room.");
            throw new IllegalArgumentException(ErrorMessages.COUNT_OF_BEDS_NOT_EQUAL_BEDS_ADDED);
        }
    }

    private Room getRoom(UpdateRoomInput input) {
        return roomRepository.findById(UUID.fromString(input.getId()))
            .orElseThrow(() -> new NotFoundException(ErrorMessages.ROOM_NOT_FOUND));
    }

    private List<Bed> getBeds(List<String> input) {
        return input.stream()
            .map(BedSize::getByCode)
            .map(bedSize -> {
                Bed bed = modelMapper.map(bedSize, Bed.class);
                return bedRepository.findBedByBedSize(BedSize.getByCode(bedSize.toString()))
                    .map(foundBed -> {
                        bed.setId(foundBed.getId());
                        bed.setBedSize(foundBed.getBedSize());
                        return bed;
                    })
                    .orElseThrow(() -> new NotFoundException(ErrorMessages.BED_NOT_FOUND));
            })
            .toList();
    }
}
