package com.tinqinacademy.hotel.restexport;

import com.tinqinacademy.hotel.api.operations.addroom.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.restapiroutes.RestApiRoutes;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
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
     ResponseEntity<?> getAvailableRooms(@RequestParam(value = "startDate") LocalDate startDate,
                                         @RequestParam(value = "endDate") LocalDate endDate,
                                         @RequestParam(value = "bedCount") Integer bedCount,
                                         @RequestParam(value = "bedSize") List<String> beds,
                                         @RequestParam(value = "bathroomType") String bathroomType);

    @GetMapping(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS_BY_ID)
//    @RequestLine(value = "GET /api/v1/hotel/{roomId}",decodeSlash = false)
    ResponseEntity<?> getRoomInfoById(@PathVariable("roomId") String roomId);

    @PostMapping(RestApiRoutes.HOTEL_BOOK_ROOM)
    ResponseEntity<?> bookRoom(@PathVariable("roomId") String roomId,
                               @RequestBody BookRoomInput bookRoomInput);

    @DeleteMapping(RestApiRoutes.HOTEL_UNBOOK_ROOM)
    ResponseEntity<?> unbook(@PathVariable("bookingId") String bookingId);

    @PostMapping(RestApiRoutes.SYSTEM_REGISTER_VISITOR)
    ResponseEntity<?> register(@RequestBody RegisterVisitorInput registerVisitorInput);

    @GetMapping(RestApiRoutes.SYSTEM_REPORT_VISITOR_INFO)
    ResponseEntity<?> report(
        @RequestParam(required = false) Optional<LocalDate> startDate,
        @RequestParam(required = false) Optional<LocalDate> endDate,
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
    ResponseEntity<?> addRoom(@RequestBody AddRoomInput addRoomInput);

    @PutMapping(RestApiRoutes.SYSTEM_UPDATE_ROOM)
    ResponseEntity<?> updateRoom(@RequestBody UpdateRoomInput updateRoomInput,
                                 @PathVariable("roomId") String roomId);

    @PatchMapping(RestApiRoutes.SYSTEM_UPDATE_ROOM_PARTIALLY)
    ResponseEntity<?> updateRoomPartially(@PathVariable("roomId") String roomId,
                                          @RequestBody UpdateRoomPartiallyInput updateRoomPartiallyInput);

    @DeleteMapping(RestApiRoutes.SYSTEM_DELETE_ROOM)
    public ResponseEntity<?> deleteRoom(@PathVariable("roomId") String roomId);
}
