package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {

    @Query("SELECT b FROM Booking b JOIN Room r on b.roomBooked.id = r.id "
        + "WHERE b.startDate = :startDate AND b.endDate = :endDate AND r.roomNo = :roomNo")
    Optional<Booking> findBookingByInputCriteria(@Param("startDate") LocalDate startDate,
                                                @Param("endDate")LocalDate endDate,
                                                @Param("roomNo")String roomNo);

    List<Booking> findBookingByRoomBookedId(UUID id);

    @Query("SELECT b FROM Booking b WHERE b.roomBooked.id = :id AND b.startDate>=:currentDate")
    List<Booking> findBookingByRoomId(@Param("id") UUID id,
                                      @Param("currentDate") LocalDate currentDate);


}
