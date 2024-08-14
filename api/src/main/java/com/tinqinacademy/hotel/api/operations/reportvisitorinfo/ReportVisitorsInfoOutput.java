package com.tinqinacademy.hotel.api.operations.reportvisitorinfo;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReportVisitorsInfoOutput implements OperationOutput {
    List<ReportVisitorsInfoOutputInfo> visitorsReport;
}
