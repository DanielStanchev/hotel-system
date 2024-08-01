package com.tinqinacademy.hotel.core.conversion;

import com.tinqinacademy.hotel.api.operations.getavailablerooms.GetAvailableRoomsOutput;
import com.tinqinacademy.hotel.persistence.entity.Room;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GetAvailableRoomsOutputConverter extends BaseConverter<Room, GetAvailableRoomsOutput.GetAvailableRoomsOutputBuilder>{
    @Override
    public GetAvailableRoomsOutput.GetAvailableRoomsOutputBuilder convertObject(Room source) {
        return GetAvailableRoomsOutput.builder()
            .roomIds(Collections.singletonList(source.getRoomNo()));
    }
}
