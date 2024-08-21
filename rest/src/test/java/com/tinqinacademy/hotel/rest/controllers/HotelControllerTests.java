package com.tinqinacademy.hotel.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.restapiroutes.RestApiRoutes;
import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.entity.Booking;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.initializer.BedInitializer;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
public class HotelControllerTests {

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

        Booking booking = Booking.builder()
            .startDate(LocalDate.of(2024, 11, 22))
            .endDate(LocalDate.of(2024, 11, 25))
            .roomBooked(room)
            .userId(java.util.UUID.fromString(("756f33c6-a3f7-4b38-9a88-9e8729bfd723")))
            .guests(new ArrayList<>())
            .build();
        bookingRepository.save(booking);
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        bedRepository.deleteAll();
    }

    @Test
    void getAvailableRoomsOk() throws Exception {
        mvc.perform(get(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bathroomType","private")
                        .param("startDate","2024-11-27")
                        .param("endDate","2024-11-30")
                        .param("bedCount","2")
                        .param("beds","kingSize","queenSize")
                        .characterEncoding("UTF-8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roomIds", hasSize(1)));
    }

    @Test
    void getAvailableRoomsNotFound() throws Exception {
        mvc.perform(get(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS+"/aaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-08-20")
                        .param("endDate","2024-08-21")
                        .param("bedCount","1")
                        .param("bedSize","double")
                        .param("bathroomType","private")
                        .characterEncoding("UTF-8"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAvailableRoomsBadRequest() throws Exception {
        mvc.perform(get(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-08-20")
                        .param("endDate","2024-08-21")
                        .param("bedCount","1")
                        .param("bedSize","double")
                        .param("bathroomType","aaaaa")
                        .characterEncoding("UTF-8"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getRoomInfoByIdOk() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();
        mvc.perform(get(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS_BY_ID, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.price").exists())
            .andExpect(jsonPath("$.floor").exists())
            .andExpect(jsonPath("$.bathroomType").exists())
            .andExpect(jsonPath("$.bedCount").exists())
            .andExpect(jsonPath("$.beds").isArray())
            .andExpect(jsonPath("$.beds", hasSize(2)))
            .andExpect(jsonPath("$.getRoomDatesOccupiedInfo").isArray())
            .andExpect(jsonPath("$.getRoomDatesOccupiedInfo", hasSize(1)))
            .andExpect(status().isOk());
    }

    @Test
    void getRoomInfoByIdNotFound() throws Exception {
        UUID wrongRoomId = UUID.fromString("756f33c6-a3f7-4b38-9a88-9e8729bfd723");
        mvc.perform(get(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS_BY_ID, wrongRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getRoomInfoByIdBadRequest() throws Exception {
        UUID wrongRoomId = UUID.fromString("756f33c6-a3f7-4b38-9a88-9e8729bfd723");
        mvc.perform(get(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS_BY_ID, "aaa"+wrongRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void bookRoomCreated() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();
        UUID mockUserId = UUID.fromString("756f33c6-a3f7-4b38-9a88-9e8729bfd723");

        BookRoomInput input = BookRoomInput.builder()
            .roomId(roomId)
            .startDate(LocalDate.of(2024,12,13))
            .endDate(LocalDate.of(2024,12,15))
            .userId(String.valueOf(mockUserId))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.HOTEL_BOOK_ROOM,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isCreated());
    }

    @Test
    void bookRoomNotFound() throws Exception {
        UUID wrongRoomId = UUID.fromString("756f33c6-a3f7-4b38-9a88-9e8729bfd723");
        UUID mockUserId = UUID.fromString("756f33c6-a3f7-4b38-9a88-9e8729bfd723");

        BookRoomInput input = BookRoomInput.builder()
            .roomId(String.valueOf(wrongRoomId))
            .startDate(LocalDate.of(2024,12,13))
            .endDate(LocalDate.of(2024,12,15))
            .userId(String.valueOf(mockUserId))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.HOTEL_BOOK_ROOM,wrongRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isNotFound());
    }

    @Test
    void bookRoomBadRequest() throws Exception {
        UUID wrongRoomId = UUID.fromString("756f33c6-a3f7-4b38-9a88-9e8729bfd723");
        UUID mockUserId = UUID.fromString("756f33c6-a3f7-4b38-9a88-9e8729bfd723");

        BookRoomInput input = BookRoomInput.builder()
            .roomId(String.valueOf(wrongRoomId))
            .startDate(LocalDate.of(2024,12,13))
            .endDate(LocalDate.of(2024,12,15))
            .userId(String.valueOf(mockUserId))
            .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.HOTEL_BOOK_ROOM,"aaa",wrongRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isBadRequest());
    }

    @Test
    void unbookRoomOk() throws Exception {
        String bookingId = bookingRepository.findAll().get(0).getId().toString();
        UnbookRoomInput input = UnbookRoomInput.builder()
            .id(bookingId)
            .build();
        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(delete(RestApiRoutes.HOTEL_BOOK_ROOM,bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isOk());
    }

    @Test
    void unbookRoomNotFound() throws Exception {
        UUID wrongBookingId = UUID.fromString("756f33c6-a3f7-4b38-9a88-9e8729bfd723");
        UnbookRoomInput input = UnbookRoomInput.builder()
            .id(String.valueOf(wrongBookingId))
            .build();
        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(delete(RestApiRoutes.HOTEL_BOOK_ROOM,wrongBookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isNotFound());
    }

    @Test
    void unbookRoomBadRequest() throws Exception {
        UUID wrongBookingId = UUID.fromString("756f33c6-a3f7-4b38-9a88-9e8729bfd723");
        UnbookRoomInput input = UnbookRoomInput.builder()
            .id(String.valueOf(wrongBookingId))
            .build();
        String serializedInput = objectMapper.writeValueAsString(input);

        mvc.perform(delete(RestApiRoutes.HOTEL_BOOK_ROOM,"aaa"+wrongBookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(serializedInput))
            .andExpect(status().isBadRequest());
    }
}
