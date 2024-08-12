package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitor;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.core.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.exception.ErrorMapper;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.entity.Booking;
import com.tinqinacademy.hotel.persistence.entity.Guest;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.GuestRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class RegisterVisitorOperationProcessor extends BaseOperationProcessor implements RegisterVisitor {

    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;

    public RegisterVisitorOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper,
                                             GuestRepository guestRepository, BookingRepository bookingRepository) {
        super(validator, conversionService,errorMapper);
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Either<ErrorWrapper,RegisterVisitorOutput> process(RegisterVisitorInput input) {
        log.info("Start register input:{}.", input);
        return validateInput(input).flatMap(validated-> registerVisitor(input));
    }

    private Either<ErrorWrapper, RegisterVisitorOutput> registerVisitor(RegisterVisitorInput input) {
        return Try.of(()->{
        Booking bookingMade = getBookingByInputCriteria(input);
        Guest registerVisitor = getConvertedGuestByInput(input);
        guestRepository.save(registerVisitor);
        bookingMade.getGuests().add(registerVisitor);
        bookingRepository.save(bookingMade);
        RegisterVisitorOutput result = RegisterVisitorOutput.builder()
                .build();

        log.info("End register output:{}.", result);
        return result;

        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private Guest getConvertedGuestByInput(RegisterVisitorInput input) {
       return conversionService.convert(input, Guest.GuestBuilder.class).build();
    }

    private Booking getBookingByInputCriteria(RegisterVisitorInput input) {
        return bookingRepository
            .findBookingByInputCriteria(input.getStartDate(), input.getEndDate(),input.getRoomNo())
            .orElseThrow(()-> new NotFoundException(ErrorMessages.BOOKING_NOT_FOUND));
    }
}
