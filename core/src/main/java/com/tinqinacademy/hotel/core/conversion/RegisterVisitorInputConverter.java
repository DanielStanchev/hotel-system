package com.tinqinacademy.hotel.core.conversion;

import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.persistence.entity.Guest;
import org.springframework.stereotype.Component;

@Component
public class RegisterVisitorInputConverter extends BaseConverter<RegisterVisitorInput, Guest.GuestBuilder>{
    @Override
    public Guest.GuestBuilder convertObject(RegisterVisitorInput input) {
        return Guest.builder()
            .birthDate(input.getBirthDate())
            .firstName(input.getFirstName())
            .lastName(input.getLastName())
            .idCardIssueAuthority(input.getIdCardIssueAuthority())
            .idCardValidity(input.getIdCardValidity())
            .idCardIssueDate(input.getIdCardIssueDate())
            .idCardNo(input.getIdCardNo());
    }
}
