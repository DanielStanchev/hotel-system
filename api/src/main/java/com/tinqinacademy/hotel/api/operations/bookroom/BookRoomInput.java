package com.tinqinacademy.hotel.api.operations.bookroom;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BookRoomInput implements OperationInput {

    @JsonIgnore
    @NotBlank
    private String roomId;

    @NotNull
    @FutureOrPresent(message = "Start date should be present or future.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @FutureOrPresent(message = "Start date should be present or future.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull
    private String userId;
}
