package com.tinqinacademy.hotel.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.operations.addroom.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.restapiroutes.RestApiRoutes;
import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.entity.Booking;
import com.tinqinacademy.hotel.persistence.entity.Guest;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.initializer.BedInitializer;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.GuestRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
public class SystemControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private BedInitializer bedInitializer;

    @BeforeEach
    public void setup() throws Exception {
        bedInitializer.run(new ApplicationArguments() {
            @Override
            public String[] getSourceArgs() {
                return new String[0];
            }

            @Override
            public Set<String> getOptionNames() {
                return Collections.emptySet();
            }

            @Override
            public boolean containsOption(String name) {
                return false;
            }

            @Override
            public List<String> getOptionValues(String name) {
                return null;
            }

            @Override
            public List<String> getNonOptionArgs() {
                return Collections.emptyList();
            }
        });

        Bed bed1 = bedRepository.findBedByBedSize(BedSize.KING_SIZE).get();
        Bed bed2 = bedRepository.findBedByBedSize(BedSize.QUEEN_SIZE).get();

        Room room = Room.builder()
            .bathroomType(BathroomType.PRIVATE)
            .floor(2)
            .roomNo("2a")
            .price(BigDecimal.valueOf(200))
            .beds(List.of(bed1, bed2))
            .build();

        roomRepository.save(room);

        Guest guest = Guest.builder()
            .firstName("Ivancho")
            .lastName("Ivanov")
            .phoneNo("08976542")
            .idCardValidity(LocalDate.of(2029,11,13))
            .idCardIssueAuthority("MVR-Varna")
            .idCardNo("986524")
            .idCardIssueDate(LocalDate.of(2022,10,11))
            .birthDate(LocalDate.of(1996,9,12))
            .build();
        guestRepository.save(guest);

        Booking booking = Booking.builder()
            .startDate(LocalDate.of(2024, 11, 22))
            .endDate(LocalDate.of(2024, 11, 25))
            .roomBooked(room)
            .userId(java.util.UUID.fromString(("756f33c6-a3f7-4b38-9a88-9e8729bfd723")))
            .guests(List.of(guest))
            .build();
        bookingRepository.save(booking);
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        bedRepository.deleteAll();
        guestRepository.deleteAll();
    }

    @Test
    void registerCreated() throws Exception {
        Room room = roomRepository.findAll().get(0);

        RegisterVisitorInput guest = RegisterVisitorInput.builder()
            .roomNo(room.getRoomNo())
            .startDate(LocalDate.of(2024, 11, 22))
            .endDate(LocalDate.of(2024, 11, 25))
            .firstName("Daniel")
            .lastName("Stanchev")
            .birthDate(LocalDate.of(1995,12,17))
            .phoneNo("0896356053")
            .idCardNo("951207")
            .idCardValidity(LocalDate.of(2028, 11, 13))
            .idCardIssueAuthority("MVR-Varna")
            .idCardIssueDate(LocalDate.of(2019, 8, 9))
            .build();

        String serializedInput = objectMapper.writeValueAsString(guest);

        mvc.perform(post(RestApiRoutes.SYSTEM_REGISTER_VISITOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isCreated());
    }

    @Test
    void registerNotFound() throws Exception {
        Room room = roomRepository.findAll().get(0);

        RegisterVisitorInput guest = RegisterVisitorInput.builder()
            .roomNo(room.getRoomNo())
            .startDate(LocalDate.of(2024, 12, 22))
            .endDate(LocalDate.of(2024, 12, 25))
            .firstName("Daniel")
            .lastName("Stanchev")
            .birthDate(LocalDate.of(1995,12,17))
            .phoneNo("0896356053")
            .idCardNo("951207")
            .idCardValidity(LocalDate.of(2028, 11, 13))
            .idCardIssueAuthority("MVR-Varna")
            .idCardIssueDate(LocalDate.of(2019, 8, 9))
            .build();

        String serializedInput = objectMapper.writeValueAsString(guest);

        mvc.perform(post(RestApiRoutes.SYSTEM_REGISTER_VISITOR+"aaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isNotFound());
    }

    @Test
    void registerBadRequest() throws Exception {
        Room room = roomRepository.findAll().get(0);

        RegisterVisitorInput guest = RegisterVisitorInput.builder()
            .roomNo(room.getRoomNo())
            .startDate(LocalDate.of(2024, 12, 22))
            .endDate(LocalDate.of(2024, 12, 25))
            .firstName("Daniel")
            .lastName("Stanchev")
            .birthDate(null)
            .phoneNo("0896356053")
            .idCardNo("951207")
            .idCardValidity(LocalDate.of(2028, 11, 13))
            .idCardIssueAuthority("MVR-Varna")
            .idCardIssueDate(LocalDate.of(2019, 8, 9))
            .build();

        String serializedInput = objectMapper.writeValueAsString(guest);

        mvc.perform(post(RestApiRoutes.SYSTEM_REGISTER_VISITOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isBadRequest());
    }

    @Test
    void reportVisitorsInfoOk() throws Exception {
        mvc.perform(get(RestApiRoutes.SYSTEM_REPORT_VISITOR_INFO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("roomNo","2a")
                        .characterEncoding("UTF-8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.visitorsReport", hasSize(1)));
    }

    @Test
    void reportVisitorsInfoNotFound() throws Exception {
        mvc.perform(get(RestApiRoutes.SYSTEM_REPORT_VISITOR_INFO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("roomNo","1a")
                        .characterEncoding("UTF-8"))
            .andExpect(status().isNotFound());
    }

    @Test
    void reportVisitorsInfoBadRequest() throws Exception {
        mvc.perform(get(RestApiRoutes.SYSTEM_REPORT_VISITOR_INFO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("idCardNo","1111")
                        .characterEncoding("UTF-8"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addRoomCreated() throws Exception{
        AddRoomInput input = AddRoomInput.builder()
            .bedCount(1)
            .bathroomType("private")
            .floor(2)
            .roomNo("2c")
            .price(BigDecimal.valueOf(999))
            .beds(List.of("single"))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.SYSTEM_CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isCreated());
    }

    @Test
    void addRoomNotFound() throws Exception{
        AddRoomInput input = AddRoomInput.builder()
            .bedCount(1)
            .bathroomType("private")
            .floor(2)
            .roomNo("2c")
            .price(BigDecimal.valueOf(999))
            .beds(List.of("111"))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(post("/hotel/adddd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isNotFound());
    }

    @Test
    void addRoomBadRequest() throws Exception{
        AddRoomInput input = AddRoomInput.builder()
            .bedCount(1)
            .bathroomType("private")
            .floor(2)
            .roomNo("2c")
            .price(BigDecimal.valueOf(999))
            .beds(List.of("wrongBed"))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.SYSTEM_CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoomOk() throws Exception{
        Room room = roomRepository.findAll().get(0);
        List<Booking> roomBooking = bookingRepository.findBookingByRoomBookedId(room.getId());
        bookingRepository.deleteAll(roomBooking);
        Integer newBedCount = 1;
        String newBathroomType = BathroomType.SHARED.toString();
        String newRoomNo = "1a";
        Integer newFloor = 1;
        BigDecimal newPrice = BigDecimal.valueOf(222);
        String newBed = BedSize.SINGLE.toString();

        UpdateRoomInput input = UpdateRoomInput.builder()
            .id(String.valueOf(room.getId()))
            .bedCount(newBedCount)
            .bathroomType(newBathroomType)
            .floor(newFloor)
            .roomNo(newRoomNo)
            .price(newPrice)
            .beds(List.of(newBed))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(put(RestApiRoutes.SYSTEM_UPDATE_ROOM, room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(room.getId().toString()));
    }

    @Test
    void updateRoomNotFound() throws Exception{
        String wrongRoomId = "756f33c6-a3f7-4b38-9a88-9e8729bfd723";
        Integer newBedCount = 1;
        String newBathroomType = BathroomType.SHARED.toString();
        String newRoomNo = "1a";
        Integer newFloor = 1;
        BigDecimal newPrice = BigDecimal.valueOf(222);
        String newBed = BedSize.SINGLE.toString();

        UpdateRoomInput input = UpdateRoomInput.builder()
            .id(wrongRoomId)
            .bedCount(newBedCount)
            .bathroomType(newBathroomType)
            .floor(newFloor)
            .roomNo(newRoomNo)
            .price(newPrice)
            .beds(List.of(newBed))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(put(RestApiRoutes.SYSTEM_UPDATE_ROOM, wrongRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateRoomBadRequest() throws Exception{
        Room room = roomRepository.findAll().get(0);
        List<Booking> roomBooking = bookingRepository.findBookingByRoomBookedId(room.getId());
        bookingRepository.deleteAll(roomBooking);
        Integer newBedCount = 1;
        String newBathroomType = BathroomType.SHARED.toString();
        String newRoomNo = "1a";

        BigDecimal newPrice = BigDecimal.valueOf(222);
        String newBed = BedSize.SINGLE.toString();

        UpdateRoomInput input = UpdateRoomInput.builder()
            .id(String.valueOf(room.getId()))
            .bedCount(newBedCount)
            .bathroomType(newBathroomType)
            .floor(99)
            .roomNo(newRoomNo)
            .price(newPrice)
            .beds(List.of(newBed))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(put(RestApiRoutes.SYSTEM_UPDATE_ROOM, room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoomPartiallyOk() throws Exception{
        Room room = roomRepository.findAll().get(0);
        List<Booking> roomBooking = bookingRepository.findBookingByRoomBookedId(room.getId());
        bookingRepository.deleteAll(roomBooking);
        BigDecimal newPrice = BigDecimal.valueOf(222);

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
            .id(String.valueOf(room.getId()))
            .bedCount(2)
            .bathroomType(room.getBathroomType().toString())
            .floor(room.getFloor())
            .roomNo(room.getRoomNo())
            .price(newPrice)
            .beds(room.getBeds().stream().map(Bed::toString).toList())
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(patch(RestApiRoutes.SYSTEM_UPDATE_ROOM_PARTIALLY, room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(room.getId().toString()));
    }

    @Test
    void updateRoomPartiallyNotFound() throws Exception{
        String wrongRoomId = "756f33c6-a3f7-4b38-9a88-9e8729bfd723";
        String newBathroomType = BathroomType.SHARED.toString();
        String newRoomNo = "1a";
        BigDecimal newPrice = BigDecimal.valueOf(222);
        String newBed = BedSize.SINGLE.toString();

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
            .id(wrongRoomId)
            .bedCount(1)
            .bathroomType(newBathroomType)
            .floor(2)
            .roomNo(newRoomNo)
            .price(newPrice)
            .beds(List.of(newBed))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(patch(RestApiRoutes.SYSTEM_UPDATE_ROOM_PARTIALLY, wrongRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateRoomPartiallyBadRequest() throws Exception{
        Room room = roomRepository.findAll().get(0);
        List<Booking> roomBooking = bookingRepository.findBookingByRoomBookedId(room.getId());
        bookingRepository.deleteAll(roomBooking);
        BigDecimal newPrice = BigDecimal.valueOf(-100);

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
            .id(String.valueOf(room.getId()))
            .bedCount(2)
            .bathroomType(room.getBathroomType().toString())
            .floor(room.getFloor())
            .roomNo(room.getRoomNo())
            .price(newPrice)
            .beds(room.getBeds().stream().map(Bed::toString).toList())
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(patch(RestApiRoutes.SYSTEM_UPDATE_ROOM_PARTIALLY, room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteRoomOk() throws Exception{
        Room room = roomRepository.findAll().get(0);
        List<Booking> roomBooking = bookingRepository.findBookingByRoomBookedId(room.getId());
        bookingRepository.deleteAll(roomBooking);

        mvc.perform(delete(RestApiRoutes.SYSTEM_DELETE_ROOM,room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isOk());
    }

    @Test
    void deleteRoomNotFound() throws Exception{
        String wrongRoomId = "756f33c6-a3f7-4b38-9a88-9e8729bfd723";

        mvc.perform(delete(RestApiRoutes.SYSTEM_DELETE_ROOM,wrongRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteRoomBadRequest() throws Exception{
        String wrongRoomId = "756f33c6-a3f7-4b38-9a88-9e8729bfd723";

        mvc.perform(delete(RestApiRoutes.SYSTEM_DELETE_ROOM,"aaaa"+wrongRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isBadRequest());
    }
}
