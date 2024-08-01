package com.tinqinacademy.hotel.core.conversion;

import com.tinqinacademy.hotel.api.operations.registeruser.RegisterUserInput;
import com.tinqinacademy.hotel.persistence.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserInputConverter extends BaseConverter<RegisterUserInput, User.UserBuilder> {
    @Override
    public User.UserBuilder convertObject(RegisterUserInput input){

        return User.builder()
            .birthDate(input.getBirthDate())
            .email(input.getEmail())
            .firstName(input.getFirstName())
            .lastName(input.getLastName())
            .password(input.getPassword())
            .phoneNo(input.getPhoneNo())
            .username(input.getUsername());
    }
}
