package com.tinqinacademy.hotel.persistence.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BedSize {

    SINGLE("single",1),
    SMALL_DOUBLE("smallDouble",2),
    DOUBLE("double",2),
    QUEEN_SIZE("queenSize",3),
    KING_SIZE("kingSize",4),
    UNKNOWN("",0);

    private final String code;
    private final Integer capacity;

    BedSize(String code, Integer capacity) {
        this.code = code;
        this.capacity = capacity;
    }

    public String getCode() {
        return code;
    }

    @JsonCreator
    public static BedSize getByCode(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(BedSize.values())
            .filter(bedSize -> bedSize.toString().equals(code))
            .findFirst()
            .orElse(BedSize.UNKNOWN);
    }

    @JsonValue
    @Override
        public String toString() {
            return this.code;
        }
}
