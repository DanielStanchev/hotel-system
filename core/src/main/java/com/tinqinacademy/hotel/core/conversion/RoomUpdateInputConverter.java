package com.tinqinacademy.hotel.core.conversion;

import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import org.springframework.stereotype.Component;

@Component
public class RoomUpdateInputConverter extends BaseConverter<UpdateRoomInput, Room.RoomBuilder>{
    @Override
    public Room.RoomBuilder convertObject(UpdateRoomInput input) {

        return Room.builder()
            .bathroomType(BathroomType.getByCode(input.getBathroomType()))
            .floor(input.getFloor())
            .price(input.getPrice())
            .roomNo(input.getRoomNo());
    }
}
