package com.tinqinacademy.hotel.api.operations.updateroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.customvalidation.bathroomtypevalidation.BathroomTypeValidation;
import com.tinqinacademy.hotel.api.customvalidation.bedsizevalidation.BedSizeValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UpdateRoomInput implements OperationInput {

    @JsonIgnore
    @NotBlank
    private String id;

    @Min(value = 1, message = "Min bed count should be no more than 1")
    @Max(value = 5, message = "Max bed count should be no more than 5")
    @NotNull
    private Integer bedCount;

    @NotNull
    @BathroomTypeValidation
    @Size(min = 2, max = 15,message = "Enter a valid bathroom type.")
    private String bathroomType;

    @NotNull
    @Min(value = -2,message = "Lower floor is -2.")
    @Max(value = 16,message = "Highest floor is 16.")
    private Integer floor;

    @NotNull
    @Size(min = 2, max = 20,message = "Room number should be between 2 and 20 symbols.")
    private String roomNo;

    @NotNull
    @Positive(message = "Price should be positive.")
    private BigDecimal price;

    //additional so admin could update beds in the room as well
    @NotEmpty
    @Builder.Default
    private List<@BedSizeValidation @Valid String> beds = new ArrayList<>();
}

