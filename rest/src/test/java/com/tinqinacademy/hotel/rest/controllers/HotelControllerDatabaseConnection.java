//package com.tinqinacademy.hotel.rest.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
//import com.tinqinacademy.hotel.api.operations.unbookroom.UnbookRoomInput;
//import com.tinqinacademy.hotel.api.restapiroutes.RestApiRoutes;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class HotelControllerDatabaseConnection {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void getAvailableRoomsOK() throws Exception {
//
//        this.mockMvc.perform(get(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS).contentType(MediaType.APPLICATION_JSON)
//                                 .param("startDate", LocalDate.now().toString())
//                                 .param("endDate", LocalDate.now().toString())
//                                 .param("bedCount", "2")
//                                 .param("bedSize", "single")
//                                 .param("bathroomType", "private"))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    void getAvailableRoomsById() throws Exception {
//
//        this.mockMvc.perform(get(RestApiRoutes.HOTEL_GET_AVAILABLE_ROOMS_BY_ID, "roomId").contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    void bookRoom() throws Exception {
//        BookRoomInput input = BookRoomInput.builder()
//            .endDate(LocalDate.now())
//            .firstName("George")
//            .startDate(LocalDate.now())
//            .phoneNo("okaoksoakoska")
//            .lastName("iajsiajsijas")
//            .build();
//
//        String serializedInput = objectMapper.writeValueAsString(input);
//
//        this.mockMvc.perform(post(RestApiRoutes.HOTEL_BOOK_ROOM, "roomId")
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .content(serializedInput))
//            .andExpect(status().isCreated());
//    }
//
//    @Test
//    void unbook() throws Exception {
//
//        UnbookRoomInput input = UnbookRoomInput.builder()
//            .build();
//
//        String serializedInput = objectMapper.writeValueAsString(input);
//
//        this.mockMvc.perform(delete(RestApiRoutes.HOTEL_UNBOOK_ROOM, "bookingId")
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .content(serializedInput))
//            .andExpect(status().isOk());
//    }
//}
//
//
