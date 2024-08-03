package com.tinqinacademy.hotel.api.operations.updateroompartially;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.enumvalidation.BathroomTypeValidation;
import com.tinqinacademy.hotel.api.enumvalidation.BedSizeValidation;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateRoomPartiallyInput implements OperationInput {

    @JsonIgnore
    @NotBlank
    private String id;

    @Min(value = 1, message = "Minimum bed count is 1.")
    @Max(value = 5, message = "Maximum bed count is 5.")
    @Nullable
    private Integer bedCount;

    @Size(min = 2, max = 15, message = "Enter a valid bathroom type.")
    @BathroomTypeValidation(optional = true)
    private String bathroomType;

    @Min(value = -2, message = "Minimum floor is -2.")
    @Max(value = 16, message = "Maximum floor is 16.")
    private Integer floor;


    @Size(min = 2, max = 20, message = "Room number should be between 2 and 20 symbols.")
    private String roomNo;

    @Positive(message = "Price should be positive.")
    private BigDecimal price;

    @Builder.Default
    private List<@BedSizeValidation(optional = true) @Valid String> beds = new ArrayList<>();

}
