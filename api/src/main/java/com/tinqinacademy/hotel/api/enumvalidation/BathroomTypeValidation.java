package com.tinqinacademy.hotel.api.enumvalidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // the annotation will be applied to fields
@Retention(RetentionPolicy.RUNTIME) // will be available on runtime
@Constraint(validatedBy = BathroomTypeValidator.class) // implement custom validation logic from the class
public @interface BathroomTypeValidation {
    String message() default "Invalid bathroom type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean optional() default false;
}