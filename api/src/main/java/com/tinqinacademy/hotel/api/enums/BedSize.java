package com.tinqinacademy.hotel.api.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
@Getter
public enum BedSize {

    SINGLE("single"),
    SMALL_DOUBLE("smallDouble"),
    DOUBLE("double"),
    QUEEN_SIZE("queenSize"),
    KING_SIZE("kingSize"),
    UNKNOWN("unknown");

    private final String code;


    BedSize(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @JsonCreator
    public static BedSize getByCode(String code) {
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
