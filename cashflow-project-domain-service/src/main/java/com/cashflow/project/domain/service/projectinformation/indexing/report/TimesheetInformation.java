package com.cashflow.project.domain.service.projectinformation.indexing.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 16, 2016, 5:56:01 AM
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimesheetInformation {

    private int totalProjectTime;

    private int totalBillableTime;

    private int totalNonBillableTime;

}
