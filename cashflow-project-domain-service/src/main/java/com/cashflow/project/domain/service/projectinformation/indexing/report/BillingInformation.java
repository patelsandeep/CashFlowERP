package com.cashflow.project.domain.service.projectinformation.indexing.report;

import com.anosym.common.Amount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 16, 2016, 6:14:43 AM
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillingInformation {

    private Amount totalProjectBillings;

    private Amount totalPaymentsReceived;

    private Amount totalAccountReceivables60Days;

    private Amount totalAccountReceivables90Days;

    private Amount projectBillingInDispute;

}
