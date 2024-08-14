package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.exceptionmodel.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.addroom.AddRoom;
import com.tinqinacademy.hotel.api.operations.addroom.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.AddRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoom;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitor;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoom;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartially;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartiallyOutput;
import com.tinqinacademy.hotel.api.operations.reportvisitorinfo.ReportVisitorsInfo;
import com.tinqinacademy.hotel.api.operations.reportvisitorinfo.ReportVisitorsInfoInput;
import com.tinqinacademy.hotel.api.operations.reportvisitorinfo.ReportVisitorsInfoOutput;
import com.tinqinacademy.hotel.api.restapiroutes.RestApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@Tag(name = "System API", description = "System related functionality.")
@RestController
public class SystemController extends BaseController{

    private final RegisterVisitor registerVisitor;
    private final ReportVisitorsInfo reportVisitorsInfo;
    private final AddRoom addRoom;
    private final UpdateRoom updateRoom;
    private final UpdateRoomPartially updateRoomPartially;
    private final DeleteRoom deleteRoom;

    public SystemController(RegisterVisitor registerVisitor, ReportVisitorsInfo reportVisitorsInfo, AddRoom addRoom, UpdateRoom updateRoom,
                            UpdateRoomPartially updateRoomPartially, DeleteRoom deleteRoom) {
        this.registerVisitor = registerVisitor;
        this.reportVisitorsInfo = reportVisitorsInfo;
        this.addRoom = addRoom;
        this.updateRoom = updateRoom;
        this.updateRoomPartially = updateRoomPartially;
        this.deleteRoom = deleteRoom;
    }

    @Operation(summary = "Register a guest in existing booking.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "CREATED"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PostMapping(RestApiRoutes.SYSTEM_REGISTER_VISITOR)
    public ResponseEntity<?> register(@RequestBody RegisterVisitorInput registerVisitorInput) {

        RegisterVisitorInput input = RegisterVisitorInput.builder()
            .idCardIssueDate(registerVisitorInput.getIdCardIssueDate())
            .startDate(registerVisitorInput.getStartDate())
            .endDate(registerVisitorInput.getEndDate())
            .firstName(registerVisitorInput.getFirstName())
            .lastName(registerVisitorInput.getLastName())
            .idCardIssueAuthority(registerVisitorInput.getIdCardIssueAuthority())
            .idCardValidity(registerVisitorInput.getIdCardValidity())
            .idCardNo(registerVisitorInput.getIdCardNo())
            .phoneNo(registerVisitorInput.getPhoneNo())
            .birthDate(registerVisitorInput.getBirthDate())
            .roomNo(registerVisitorInput.getRoomNo())
            .build();

        Either<ErrorWrapper,RegisterVisitorOutput> output = registerVisitor.process(input);
        return handleResult(output,HttpStatus.CREATED);
    }

    @Operation(summary = "Get basic info by various criteria.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Not found")})
    @GetMapping(RestApiRoutes.SYSTEM_REPORT_VISITOR_INFO)
    public ResponseEntity<?> report(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate,
        @RequestParam(required = false) Optional<String> firstName,
        @RequestParam(required = false) Optional<String> lastName,
        @RequestParam(required = false) Optional<String> phoneNo,
        @RequestParam(required = false) Optional<String> idCardNo,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> idCardValidity,
        @RequestParam(required = false) Optional<String> idCardIssueAuthority,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> cardIssueDate,
        @RequestParam String roomNo
        ) {
        ReportVisitorsInfoInput input = ReportVisitorsInfoInput.builder()
            .endDate(endDate.orElse(null))
            .firstName(firstName.orElse(null))
            .idCardIssueAuthority(idCardIssueAuthority.orElse(null))
            .idCardNo(idCardNo.orElse(null))
            .idCardValidity(idCardValidity.orElse(null))
            .lastName(lastName.orElse(null))
            .phoneNo(phoneNo.orElse(null))
            .startDate(startDate.orElse(null))
            .cardIssueDate(cardIssueDate.orElse(null))
            .roomNo(roomNo)
            .build();

        Either<ErrorWrapper,ReportVisitorsInfoOutput> output = reportVisitorsInfo.process(input);
        return handleResult(output,HttpStatus.OK);
    }

    @Operation(summary = "Create a room.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "CREATED"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PostMapping(RestApiRoutes.SYSTEM_CREATE_ROOM)
    public ResponseEntity<?> addRoom(@RequestBody AddRoomInput addRoomInput) {

        AddRoomInput input = AddRoomInput.builder()
            .bedCount(addRoomInput.getBedCount())
            .bathroomType(addRoomInput.getBathroomType())
            .beds(addRoomInput.getBeds())
            .floor(addRoomInput.getFloor())
            .price(addRoomInput.getPrice())
            .roomNo(addRoomInput.getRoomNo())
            .build();

        Either<ErrorWrapper, AddRoomOutput> result = addRoom.process(input);
        return handleResult(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Update room.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PutMapping(RestApiRoutes.SYSTEM_UPDATE_ROOM)
    public ResponseEntity<?> updateRoom(@RequestBody UpdateRoomInput updateRoomInput,
                                        @PathVariable("roomId") String roomId) {

        UpdateRoomInput input = UpdateRoomInput.builder()
            .id(roomId)
            .bedCount(updateRoomInput.getBedCount())
            .bathroomType(updateRoomInput.getBathroomType())
            .floor(updateRoomInput.getFloor())
            .price(updateRoomInput.getPrice())
            .roomNo(updateRoomInput.getRoomNo())
            .beds(updateRoomInput.getBeds())
            .build();

        Either<ErrorWrapper,UpdateRoomOutput> output = updateRoom.process(input);
        return handleResult(output,HttpStatus.OK);
    }

    @Operation(summary = "Update room partially.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    @PatchMapping(RestApiRoutes.SYSTEM_UPDATE_ROOM_PARTIALLY)
    public ResponseEntity<?> updateRoomPartially(@PathVariable("roomId") String roomId,
                                                 @RequestBody UpdateRoomPartiallyInput updateRoomPartiallyInput) {

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
            .id(roomId)
            .bedCount(updateRoomPartiallyInput.getBedCount())
            .bathroomType(updateRoomPartiallyInput.getBathroomType())
            .floor(updateRoomPartiallyInput.getFloor())
            .roomNo(updateRoomPartiallyInput.getRoomNo())
            .price(updateRoomPartiallyInput.getPrice())
            .beds(updateRoomPartiallyInput.getBeds())
            .build();

        Either<ErrorWrapper,UpdateRoomPartiallyOutput> output = updateRoomPartially.process(input);
        return new ResponseEntity<>(output,HttpStatus.OK);
    }

    @Operation(summary = "Delete room.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK")
    })
    @DeleteMapping(RestApiRoutes.SYSTEM_DELETE_ROOM)
    public ResponseEntity<?> deleteRoom(@PathVariable("roomId") String roomId){

        DeleteRoomInput input = DeleteRoomInput.builder()
            .id(roomId)
            .build();

        Either<ErrorWrapper,DeleteRoomOutput> output = deleteRoom.process(input);
        return handleResult(output,HttpStatus.OK);
    }
}
