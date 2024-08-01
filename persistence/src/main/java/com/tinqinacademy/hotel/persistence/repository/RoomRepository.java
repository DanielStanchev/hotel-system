package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    @Query("SELECT r FROM Room r JOIN Booking b ON r.id = b.roomBooked.id "
        + "WHERE SIZE(r.beds) = :bedCount "
        + "AND r.bathroomType = :bathroomType "
        + "AND b.startDate <= :startDate AND b.endDate >= :endDate "
        + "AND EXISTS (SELECT bed FROM r.beds bed WHERE bed.bedSize IN :beds)")
    List<Room> findRoomByCriteria(@Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  @Param("bedCount") Integer bedCount,
                                  @Param("beds") List<BedSize> beds,
                                  @Param("bathroomType") BathroomType bathroomType);
}
