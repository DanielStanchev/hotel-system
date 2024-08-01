package com.tinqinacademy.hotel.api.operations.visitorreportinfo;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ReportVisitorsInfoInput implements OperationInput {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(min = 2, max = 30, message = "First name should be between 2 and 30 symbols.")
    private String firstName;

    @Size(min = 2,max = 30,message = "Last name should be between 2 and 30 symbols.")
    private String lastName;

    @Size(min = 2,max = 30,message = "Phone number should be between 2 and 30 symbols.")
    private String phoneNo;

    @Size(min = 2, max = 30,message = "ID card number should be between 2 and 30 symbols.")
    private String idCardNo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate idCardValidity;

    private String idCardIssueAuthority;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate cardIssueDate;

    @NotNull
    private String roomNo;
}
