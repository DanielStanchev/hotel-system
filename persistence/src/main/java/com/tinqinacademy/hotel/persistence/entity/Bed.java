package com.tinqinacademy.hotel.persistence.entity;

import com.tinqinacademy.hotel.persistence.enums.BedSize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "beds")
public class Bed extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_size", nullable = false)
    private BedSize bedSize;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Override
    public String toString() {
        return bedSize.toString();
    }
}
