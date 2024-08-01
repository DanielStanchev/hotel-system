package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BedRepository extends JpaRepository<Bed, UUID> {

    @Query(value = "SELECT COUNT(*) FROM beds WHERE bed_size = :bedSize",nativeQuery = true)
    Integer initializeBeds(String bedSize);

    Optional<Bed> findBedByBedSize(BedSize bedSize);
}
