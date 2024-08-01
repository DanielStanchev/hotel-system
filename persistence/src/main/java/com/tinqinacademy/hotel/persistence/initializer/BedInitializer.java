package com.tinqinacademy.hotel.persistence.initializer;


import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class BedInitializer implements ApplicationRunner {

    private final BedRepository bedRepository;

    public BedInitializer(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Start BedInitializer for beds");

        if (bedRepository.count() > 0) {
            log.info("Beds already initialized, skipping initialization.");
            return;
        }

        for (BedSize bedSize : BedSize.values()) {
            if (bedSize.getCode()
                .isEmpty()) {
                continue;
            }

            Integer count = bedRepository.initializeBeds(bedSize.toString());

            if (count == null || count == 0) {
                Bed bed = Bed.builder()
                    .bedSize(bedSize)
                    .capacity(bedSize.getCapacity())
                    .build();
                bedRepository.save(bed);
            }
        }

    }
}

