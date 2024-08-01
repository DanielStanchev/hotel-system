package com.tinqinacademy.hotel.rest;


import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@ComponentScan(basePackages = "com.tinqinacademy.hotel")
@EntityScan(basePackages = "com.tinqinacademy.hotel.persistence.entity")
@EnableJpaRepositories(basePackages = "com.tinqinacademy.hotel.persistence.repository")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    public CommandLineRunner demo(BedRepository bedRepository, RoomRepository roomRepository) {
//        return args -> {
////            bed save method
////            Bed bed = Bed.builder()
////                .bedSize(BedSize.getByCode("double"))
////                .capacity(1)
////                .build();
////
////            if (bed.getBedSize() == BedSize.UNKNOWN) {
////                log.info("Error: The value '{}' is not a valid BedSize. Saving aborted.", bed.getBedSize());
////            } else {
////                bedRepository.save(bed);
////                log.info("Bed saved: {}", bed);
////            }
////            save method but update
////
////            Bed bed = Bed.builder()
////                .id(UUID.fromString("1d84fa40-5b9e-4276-8024-b7bac977939f"))
////                .bedSize(BedSize.getByCode("kingSize"))
////                .capacity(8)
////                .build();
////
////            bedRepository.save(bed);
////            log.info("Updated bed : {}", bed);
////
////
////            delete method
////            UUID bedID2 = UUID.fromString("dbf416f7-467a-40ab-aad2-9dde82a58e5d");
////            Bed bed = bedRepository.deleteById(bedID2);
////            log.info("Bedd eleted : {}", bed);
////
////            findById
////            UUID bedID2 = UUID.fromString("87c92b8e-f398-46cb-970d-3f7cf0651c64");
////            Optional<Bed> bed = bedRepository.findById(bedID2);
////            log.info("Bed: {}", bed);
//
//            //room save method
////        //Bed bed1 = bedRepository.findById(UUID.fromString("ddbc2bcc-f77b-4149-8feb-31596a435e2e")).orElse(null);
////        Bed bed2 = bedRepository.findById(UUID.fromString("d25f57a0-771b-4895-a2d0-7fc638b17304")).orElse(null);
////            Room room = Room.builder()
////                .bathroomType(BathroomType.getByCode("private"))
////                .roomNo("3b")
////                .floor(2)
////                .price(BigDecimal.valueOf(100))
////                .beds(List.of(bed2))
////                .build();
////
////            roomRepository.save(room);
////            log.info("Room created: {}", room);
//
//            //update room
//
//
//            //room findById
////            Room room = roomRepository.findById(UUID.fromString("eec5ab98-74b8-46bb-a8a6-ca80a8a77c3a")).orElse(null);
////            log.info("Room found{}",room);
//
//            //room deleteById
////            Room room = roomRepository.findById(UUID.fromString("eec5ab98-74b8-46bb-a8a6-ca80a8a77c3a")).orElse(null);
////            roomRepository.deleteById(room.getId());
////            log.info("Deleted room {}", room);
//
//            //room findALL
//
////            List<Room> rooms = roomRepository.findAllRooms();
////           log.info("Rooms: {}", rooms);
//
//            //bed findByCode
////            String code = "single";
////            Bed bed = bedRepository.findByCode(code).orElse(null);
////            log.info("Bed: {}", bed);
//
//        };
//    }
}
