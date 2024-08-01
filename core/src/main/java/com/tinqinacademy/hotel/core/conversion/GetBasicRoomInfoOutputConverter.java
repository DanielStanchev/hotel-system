package com.tinqinacademy.hotel.core.conversion;

import com.tinqinacademy.hotel.api.operations.getroominfobyid.GetRoomInfoByIdOutput;
import com.tinqinacademy.hotel.persistence.entity.Room;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GetBasicRoomInfoOutputConverter extends BaseConverter<Room, GetRoomInfoByIdOutput.GetRoomInfoByIdOutputBuilder>{
    @Override
    public GetRoomInfoByIdOutput.GetRoomInfoByIdOutputBuilder convertObject(Room source) {

        return GetRoomInfoByIdOutput.builder()
            .id(String.valueOf(source.getId()))
            .price(source.getPrice())
            .floor(source.getFloor())
            .bathroomType(source.getBathroomType().toString())
            .bedCount(source.getBeds().size())
            .beds(Collections.singletonList(source.getBeds()
                                                .toString()));
    }
}
