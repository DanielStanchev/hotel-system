package com.tinqinacademy.hotel.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum BathroomType {

    PRIVATE("private"),
    SHARED("shared"),
    UNKNOWN("");

    private final String code;

    BathroomType(String code) {
        this.code = code;
    }

    @JsonCreator
    public static BathroomType getByCode(String code) {
        return Arrays.stream(BathroomType.values())
            .filter(bathroomType -> bathroomType.toString().equals(code))
            .findFirst()
            .orElse(BathroomType.UNKNOWN);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.code;
    }
}
