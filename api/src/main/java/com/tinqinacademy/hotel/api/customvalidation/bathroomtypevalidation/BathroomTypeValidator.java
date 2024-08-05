package com.tinqinacademy.hotel.api.customvalidation.bathroomtypevalidation;

import com.tinqinacademy.hotel.api.enums.BathroomType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BathroomTypeValidator implements ConstraintValidator<BathroomTypeValidation, String> {

    private static final Set<String> VALID_BATHROOM_TYPES = EnumSet.allOf(BathroomType.class)
        .stream()
        .map(BathroomType::getCode)
        .collect(Collectors.toSet());
    private boolean optional;

    @Override
    public void initialize(BathroomTypeValidation constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(String bathroomType, ConstraintValidatorContext constraintValidatorContext) {
        if (optional && (bathroomType == null || bathroomType.isEmpty())) {
            return true; // Skip validation if optional and bed size is not provided
        }
        if (bathroomType == null || bathroomType.isEmpty()) {
            return false;
        }
        return VALID_BATHROOM_TYPES.contains(bathroomType);
    }
}

