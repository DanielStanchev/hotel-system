package com.tinqinacademy.hotel.api.operations.registeruser;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
//additional pojo
public class RegisterUserOutput implements OperationOutput {
    private String id;
}
