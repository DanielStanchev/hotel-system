package com.tinqinacademy.hotel.rest.controllers;


//user test controller

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.registeruser.RegisterUserInput;
import com.tinqinacademy.hotel.api.operations.registeruser.RegisterUserOutput;
import com.tinqinacademy.hotel.api.operations.registeruser.RegisterUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController extends BaseController {

    private final RegisterUser registerUser;

    public UserController(RegisterUser registerUser) {this.registerUser = registerUser;}

    @Operation(summary = "Register a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "CREATED"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PostMapping("user/register")
    public ResponseEntity<?> register(RegisterUserInput registerUserInput) {

        RegisterUserInput input = RegisterUserInput.builder()
            .username(registerUserInput.getUsername())
            .firstName(registerUserInput.getFirstName())
            .lastName(registerUserInput.getLastName())
            .phoneNo(registerUserInput.getPhoneNo())
            .password(registerUserInput.getPassword())
            .email(registerUserInput.getEmail())
            .birthDate(registerUserInput.getBirthDate())
            .build();

        Either<ErrorWrapper,RegisterUserOutput> output = registerUser.process(input);
        return handleResult(output, HttpStatus.CREATED);
    }
}
