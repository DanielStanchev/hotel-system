package com.tinqinacademy.hotel.rest.controllers;


import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoom;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.getavailablerooms.GetAvailableRooms;
import com.tinqinacademy.hotel.api.operations.getavailablerooms.GetAvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.getavailablerooms.GetAvailableRoomsOutput;
import com.tinqinacademy.hotel.api.operations.getroominfobyid.GetRoomInfoById;
import com.tinqinacademy.hotel.api.operations.getroominfobyid.GetRoomInfoByIdInput;
import com.tinqinacademy.hotel.api.operations.getroominfobyid.GetRoomInfoByIdOutput;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoom;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.restapiroutes.RestApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Hotel API", description = "Hotel room booking related functionality.")
@RestController
public class HotelController extends BaseController{

    private final GetAvailableRooms getAvailableRooms;
    private final GetRoomInfoById getRoomInfoById;
    private final BookRoom bookRoom;
    private final UnbookRoom unbookRoom;

    public HotelController(GetAvailableRooms getAvailableRooms, GetRoomInfoById getRoomInfoById,
                           BookRoom bookRoom, UnbookRoom unbookRoom) {
        this.getAvailableRooms = getAvailableRooms;
        this.getRoomInfoById = getRoomInfoById;
        this.bookRoom = bookRoom;
        this.unbookRoom = unbookRoom;
    }

    @Operation(summary = "Get available rooms by criteria.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Not found")})
    @GetMapping(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS)
    public ResponseEntity<?> getAvailableRooms(                      @RequestParam(value = "startDate") LocalDate startDate,
                                                                     @RequestParam(value = "endDate") LocalDate endDate,
                                                                     @RequestParam(value = "bedCount") Integer bedCount,
                                                                     @RequestParam(value = "beds") List<String> beds,
                                                                     @RequestParam(value = "bathroomType") String bathroomType) {
        GetAvailableRoomsInput input = GetAvailableRoomsInput.builder()
            .startDate(startDate)
            .endDate(endDate)
            .bedCount(bedCount)
            .beds(beds)
            .bathroomType(bathroomType)
            .build();

        Either<ErrorWrapper,GetAvailableRoomsOutput> output = getAvailableRooms.process(input);
        return handleResult(output, HttpStatus.OK);
    }

    @Operation(summary = "Get room basic info by Id.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Not found")})
    @GetMapping(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS_BY_ID)
    public ResponseEntity<?> getRoomInfoById(@PathVariable("roomId") String roomId) {

        GetRoomInfoByIdInput input = GetRoomInfoByIdInput.builder()
            .roomId(roomId)
            .build();

        Either<ErrorWrapper,GetRoomInfoByIdOutput> output = getRoomInfoById.process(input);
        return handleResult(output, HttpStatus.OK);
    }

    @Operation(summary = "Book a room.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "CREATED"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PostMapping(RestApiRoutes.HOTEL_BOOK_ROOM)
    public ResponseEntity<?> bookRoom(@PathVariable("roomId") String roomId,
                                      @RequestBody BookRoomInput bookRoomInput) {

        BookRoomInput input = BookRoomInput.builder()
            .roomId(roomId)
            .endDate(bookRoomInput.getEndDate())
            .userId(bookRoomInput.getUserId())
            .startDate(bookRoomInput.getStartDate())
            .build();

        Either<ErrorWrapper,BookRoomOutput> output = bookRoom.process(input);
        return handleResult(output,HttpStatus.CREATED);
    }

    @Operation(summary = "Unbook a room.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK")})
    @DeleteMapping(RestApiRoutes.HOTEL_UNBOOK_ROOM)
    public ResponseEntity<?> unbook(@PathVariable("bookingId") String bookingId) {

        UnbookRoomInput input = UnbookRoomInput.builder()
            .id(bookingId)
            .build();

        Either<ErrorWrapper,UnbookRoomOutput> output = unbookRoom.process(input);
        return handleResult(output,HttpStatus.OK);
    }
}
