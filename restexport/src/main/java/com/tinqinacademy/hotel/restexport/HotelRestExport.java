package com.tinqinacademy.hotel.restexport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tinqinacademy.hotel.api.operations.addroom.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.AddRoomOutput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.getavailablerooms.GetAvailableRoomsOutput;
import com.tinqinacademy.hotel.api.operations.getroominfobyid.GetRoomInfoByIdOutput;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartiallyOutput;
import com.tinqinacademy.hotel.api.operations.reportvisitorinfo.ReportVisitorsInfoOutput;
import com.tinqinacademy.hotel.api.restapiroutes.RestApiRoutes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@FeignClient(name = "hotel")
public interface HotelRestExport {

     @GetMapping(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS)
     GetAvailableRoomsOutput getAvailableRooms(@RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
                                               @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
                                               @RequestParam(value = "bedCount") Integer bedCount,
                                               @RequestParam(value = "beds") List<String> beds,
                                               @RequestParam(value = "bathroomType") String bathroomType);

    @GetMapping(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS_BY_ID)
    GetRoomInfoByIdOutput getRoomInfoById(@PathVariable("roomId") String roomId);

    @PostMapping(RestApiRoutes.HOTEL_BOOK_ROOM)
    BookRoomOutput bookRoom(@PathVariable("roomId") String roomId,
                            @RequestBody BookRoomInput bookRoomInput);

    @DeleteMapping(RestApiRoutes.HOTEL_UNBOOK_ROOM)
    UnbookRoomOutput unbook(@PathVariable("bookingId") String bookingId);

    @PostMapping(RestApiRoutes.SYSTEM_REGISTER_VISITOR)
    RegisterVisitorOutput register(@RequestBody RegisterVisitorInput registerVisitorInput);

    @GetMapping(RestApiRoutes.SYSTEM_REPORT_VISITOR_INFO)
    ReportVisitorsInfoOutput report(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate,
        @RequestParam(required = false) Optional<String> firstName,
        @RequestParam(required = false) Optional<String> lastName,
        @RequestParam(required = false) Optional<String> phoneNo,
        @RequestParam(required = false) Optional<String> idCardNo,
        @RequestParam(required = false) Optional<LocalDate> idCardValidity,
        @RequestParam(required = false) Optional<String> idCardIssueAuthority,
        @RequestParam(required = false) Optional<LocalDate> cardIssueDate,
        @RequestParam String roomNo
    );

    @PostMapping(RestApiRoutes.SYSTEM_CREATE_ROOM)
    AddRoomOutput addRoom(@RequestBody AddRoomInput addRoomInput);

    @PutMapping(RestApiRoutes.SYSTEM_UPDATE_ROOM)
    UpdateRoomOutput updateRoom(@RequestBody UpdateRoomInput updateRoomInput,
                                @PathVariable("roomId") String roomId);

    @PatchMapping(RestApiRoutes.SYSTEM_UPDATE_ROOM_PARTIALLY)
    UpdateRoomPartiallyOutput updateRoomPartially(@PathVariable("roomId") String roomId,
                                                  @RequestBody UpdateRoomPartiallyInput updateRoomPartiallyInput);

    @DeleteMapping(RestApiRoutes.SYSTEM_DELETE_ROOM)
    DeleteRoomOutput deleteRoom(@PathVariable("roomId") String roomId);
}
