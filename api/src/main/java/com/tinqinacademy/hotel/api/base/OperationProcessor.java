package com.tinqinacademy.hotel.api.base;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import io.vavr.control.Either;

public interface OperationProcessor<O extends OperationOutput, I extends OperationInput> {
    Either<ErrorWrapper, O> process(I input);
}
