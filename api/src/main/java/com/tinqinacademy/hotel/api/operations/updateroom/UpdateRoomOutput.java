package com.tinqinacademy.hotel.api.operations.updateroom;

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
public class UpdateRoomOutput implements OperationOutput {
    String id;
}
