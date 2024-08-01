package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.addroom.AddRoom;
import com.tinqinacademy.hotel.api.operations.addroom.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.AddRoomOutput;
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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Service
@Slf4j
public class AddRoomOperationProcessor extends BaseOperationProcessor implements AddRoom {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ModelMapper modelMapper;


    public AddRoomOperationProcessor(ConversionService conversionService, Validator validator, RoomRepository roomRepository,
                                     BedRepository bedRepository,
                                     ModelMapper modelMapper, ErrorMapper errorMapper) {
        super(validator, conversionService,errorMapper);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Either<ErrorWrapper, AddRoomOutput> process(AddRoomInput input) {
        log.info("Start createRoom input:{}.", input);
        return validateInput(input).flatMap(validated -> addRoom(input));
    }

    private Either<ErrorWrapper, AddRoomOutput> addRoom(AddRoomInput input) {

        return  Try.of(()-> {
                List<Bed> bedsToSave = getBeds(input.getBeds());
                Room roomToSave = getConvertedRoom(input, bedsToSave);
                checkCountOfBedCountWithCountOfBedsAdded(input, bedsToSave);
                roomRepository.save(roomToSave);

                AddRoomOutput result = AddRoomOutput.builder()
                    .id(String.valueOf(roomToSave.getId()))
                    .build();

                log.info("End createRoom output:{}.", result);
                return result;

            })
            .toEither()
            .mapLeft(throwable -> Match(throwable).of(
                Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
                Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
                Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
            ));
    }

    private static void checkCountOfBedCountWithCountOfBedsAdded(AddRoomInput input, List<Bed> bedsToSave) {
        if (!input.getBedCount()
            .equals(bedsToSave.size())) {
            throw new IllegalArgumentException(ErrorMessages.COUNT_OF_BEDS_NOT_EQUAL_BEDS_ADDED);
        }
    }

    private Room getConvertedRoom(AddRoomInput input, List<Bed> bedsToSave) {
        return conversionService.convert(input, Room.RoomBuilder.class)
            .beds(bedsToSave)
            .build();
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
