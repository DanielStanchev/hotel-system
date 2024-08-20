package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartiallyOutput;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartially;
import com.tinqinacademy.hotel.core.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.exception.ErrorMapper;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.entity.Booking;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.ConversionService;
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
public class UpdateRoomPartiallyOperationProcessor extends BaseOperationProcessor implements UpdateRoomPartially {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    public UpdateRoomPartiallyOperationProcessor(
        ConversionService conversionService, Validator validator, ErrorMapper errorMapper,
        RoomRepository roomRepository, BedRepository bedRepository, BookingRepository bookingRepository, ModelMapper modelMapper) {
        super(validator, conversionService,errorMapper);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Either<ErrorWrapper,UpdateRoomPartiallyOutput> process (UpdateRoomPartiallyInput input) {
        log.info("Start updateRoomPartially input:{}.", input);
        return validateInput(input).flatMap(validated-> updateRoomPartially(input));
    }

    private Either<ErrorWrapper, UpdateRoomPartiallyOutput> updateRoomPartially(UpdateRoomPartiallyInput input) {
        return Try.of(()->{
        Room roomToUpdate = getRoom(input);
        checkIfRoomHasBookingsInTheFuture(roomToUpdate);
        List<Bed> bedsToUpdate = getBeds(input.getBeds());
        checkIfInputBedCountEqualsNumberOfBedsToUpdate(input, bedsToUpdate);
        setUpdatedFieldsToRoomIfNotNullOrElseRemain(input, roomToUpdate);
        roomRepository.save(roomToUpdate);
        UpdateRoomPartiallyOutput result = UpdateRoomPartiallyOutput.builder()
                .id(String.valueOf(roomToUpdate.getId()))
                .build();

        log.info("End updateRoomPartially output:{}.", result);
         return result;
        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(IllegalArgumentException.class)), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST)),
            Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private void checkIfRoomHasBookingsInTheFuture(Room rooToUpdated) {
        LocalDate currentDate = LocalDate.now();
        List<Booking> bookedRoomForTheFuture = bookingRepository.findBookingByRoomId(rooToUpdated.getId(), currentDate);

        if(!bookedRoomForTheFuture.isEmpty()){
            throw new IllegalArgumentException(ErrorMessages.ROOM_ALREADY_BOOKED);
        }
    }

    private void setUpdatedFieldsToRoomIfNotNullOrElseRemain(UpdateRoomPartiallyInput input, Room roomToUpdate) {
        roomToUpdate.setPriceNotNull(input.getPrice());
        roomToUpdate.setFloorNotNull(input.getFloor());
        roomToUpdate.setRoomNoNotNull(input.getRoomNo());
        roomToUpdate.setBathroomTypeNotNull(BathroomType.getByCode(input.getBathroomType()));
        roomToUpdate.setBedsNotNull(getBeds(input.getBeds()));
    }

    private static void checkIfInputBedCountEqualsNumberOfBedsToUpdate(UpdateRoomPartiallyInput input, List<Bed> bedsToUpdate) {
        if (input.getBedCount() != null && !input.getBedCount()
            .equals(bedsToUpdate.size())) {
            log.info("The number of beds to be updated do not correspond to the beds added to the room.");
            throw new IllegalArgumentException(ErrorMessages.COUNT_OF_BEDS_NOT_EQUAL_BEDS_ADDED);
        }
    }

    private Room getRoom(UpdateRoomPartiallyInput input) {
       return roomRepository.findById(UUID.fromString(input.getId()))
           .orElseThrow(()-> new NotFoundException(ErrorMessages.ROOM_NOT_FOUND));
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
