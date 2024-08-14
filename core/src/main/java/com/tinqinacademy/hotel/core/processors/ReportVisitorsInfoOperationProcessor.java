package com.tinqinacademy.hotel.core.processors;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorMessages;
import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.reportvisitorinfo.ReportVisitorsInfo;
import com.tinqinacademy.hotel.api.operations.reportvisitorinfo.ReportVisitorsInfoInput;
import com.tinqinacademy.hotel.api.operations.reportvisitorinfo.ReportVisitorsInfoOutput;
import com.tinqinacademy.hotel.api.operations.reportvisitorinfo.ReportVisitorsInfoOutputInfo;
import com.tinqinacademy.hotel.core.exception.ErrorMapper;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.entity.Booking;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class ReportVisitorsInfoOperationProcessor implements ReportVisitorsInfo {

    private final BookingRepository bookingRepository;
    private final ErrorMapper errorMapper;

    public ReportVisitorsInfoOperationProcessor(BookingRepository bookingRepository, ErrorMapper errorMapper) {
        this.bookingRepository = bookingRepository;
        this.errorMapper = errorMapper;
    }

    @Override
    public Either<ErrorWrapper,ReportVisitorsInfoOutput> process (ReportVisitorsInfoInput input) {
        log.info("Start process input{}",input);
        return reportVisitorsInfo(input);
    }

    private Either<ErrorWrapper, ReportVisitorsInfoOutput> reportVisitorsInfo(ReportVisitorsInfoInput input) {
        return Try.of(()->{

        Specification<Booking> bookingSpec = getBookingSpecification(input);
        List<Booking> bookings = getBookingsBySpecification(bookingSpec);
        List<ReportVisitorsInfoOutputInfo> visitorInfoList = getVisitorInfoByBookings(input, bookings);
        ReportVisitorsInfoOutput result = ReportVisitorsInfoOutput.builder()
                .visitorsReport(visitorInfoList)
                .build();

        log.info("Output: {}", result);
        return result;
        }).toEither().mapLeft(throwable -> Match(throwable).of(
            Case($(instanceOf(NotFoundException.class)), errorMapper.handleError(throwable, HttpStatus.NOT_FOUND)),
            Case($(), errorMapper.handleError(throwable, HttpStatus.BAD_REQUEST))
        ));
    }

    private static List<ReportVisitorsInfoOutputInfo> getVisitorInfoByBookings(ReportVisitorsInfoInput input, List<Booking> bookings) {
        return bookings.stream()
            .flatMap(
                booking -> booking.getGuests().stream()
                    .filter(guest -> {
                        boolean firstNameMatches = input.getFirstName() == null || Objects.equals(guest.getFirstName(), input.getFirstName());
                        boolean lastNameMatches = input.getLastName() == null || Objects.equals(guest.getLastName(), input.getLastName());
                        boolean idCardNoMatches = input.getIdCardNo() == null || Objects.equals(guest.getIdCardNo(), input.getIdCardNo());
                        boolean idCardValidityMatches = input.getIdCardValidity() == null || Objects.equals(guest.getIdCardValidity(), input.getIdCardValidity());
                        boolean idCardIssueAuthorityMatches = input.getIdCardIssueAuthority() == null || Objects.equals(guest.getIdCardIssueAuthority(), input.getIdCardIssueAuthority());
                        boolean cardIssueDateMatches = input.getCardIssueDate() == null || Objects.equals(guest.getIdCardIssueDate(), input.getCardIssueDate());
                        boolean phoneNumberMatches = input.getPhoneNo() == null || Objects.equals(guest.getPhoneNo(), input.getPhoneNo());

                        return firstNameMatches && lastNameMatches && idCardNoMatches &&
                            idCardValidityMatches && idCardIssueAuthorityMatches && cardIssueDateMatches&&phoneNumberMatches;
                    })
                    .map(guest -> ReportVisitorsInfoOutputInfo.builder()
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .firstName(guest.getFirstName())
                .lastName(guest.getLastName())
                 .phoneNo(guest.getPhoneNo())
                .idCardNo(guest.getIdCardNo())
                .idCardValidity(guest.getIdCardValidity())
                .idCardIssueAuthority(guest.getIdCardIssueAuthority())
                .cardIssueDate(guest.getIdCardIssueDate())
                .build()
            ))
            .toList();
    }

    private List<Booking> getBookingsBySpecification(Specification<Booking> bookingSpec) {
        List<Booking> bookings = bookingRepository.findAll(bookingSpec);

        if(bookings.isEmpty()){
            throw new NotFoundException(ErrorMessages.BOOKING_NOT_FOUND);
        }
        return bookings;
    }

    private static Specification<Booking> getBookingSpecification(ReportVisitorsInfoInput input) {
        return Specification.where((root, query, criteriaBuilder) -> {

            Join<Booking, Room> roomJoin = root.join("roomBooked");

            Predicate roomNoPredicate = criteriaBuilder.equal(roomJoin.get("roomNo"), input.getRoomNo());

            Predicate startDatePredicate = input.getStartDate() != null ?
                                           criteriaBuilder.equal(root.get("startDate"), input.getStartDate()) : criteriaBuilder.conjunction();
            Predicate endDatePredicate = input.getEndDate() != null ?
                                         criteriaBuilder.equal(root.get("endDate"), input.getEndDate()) : criteriaBuilder.conjunction();

            return criteriaBuilder.and(roomNoPredicate, startDatePredicate, endDatePredicate);
        });
    }
}
