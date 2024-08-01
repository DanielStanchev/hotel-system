package com.tinqinacademy.hotel.api.operations.getroominfobyid;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GetRoomInfoByIdOutput implements OperationOutput {

    private String id;
    private BigDecimal price;
    private Integer floor;
    private String bathroomType;
    private Integer bedCount;
    private List<String> beds;
    private List<GetRoomDatesOccupiedInfo> getRoomDatesOccupiedInfo;
}
