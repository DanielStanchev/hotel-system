package com.tinqinacademy.hotel.api.operations.getavailablerooms;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.customvalidation.bathroomtypevalidation.BathroomTypeValidation;
import com.tinqinacademy.hotel.api.customvalidation.bedsizevalidation.BedSizeValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GetAvailableRoomsInput implements OperationInput {

    @NotNull(message = "Start date cannot be null.")
    @JsonFormat(pattern = "yyyy-MM-dd", shape=JsonFormat.Shape.STRING)
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null.")
    @JsonFormat(pattern = "yyyy-MM-dd",shape=JsonFormat.Shape.STRING)
    private LocalDate endDate;

    @NotNull(message = "Bed count cannot be null.")
    @Min(value = 1, message = "Min bed count should be no more than 1")
    @Max(value = 5, message = "Max bed count should be no more than 5")
    private Integer bedCount;

    @NotEmpty(message = "Bed sizes should correspond to bed count.")
    @Builder.Default
    private List<@BedSizeValidation @Valid String> beds = new ArrayList<>();

    @NotNull(message = "Bathroom type cannot be null.")
    @BathroomTypeValidation
    private String bathroomType;
}
