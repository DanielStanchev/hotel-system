package com.tinqinacademy.hotel.api.operations.getroominfobyid;

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
public class GetRoomDatesOccupiedInfo {

    private String startDate;
    private String endDate;
}
