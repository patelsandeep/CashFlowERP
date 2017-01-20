package com.cashflow.project.domain.service.projectinformation.indexing.report;

import com.cashflow.project.domain.project.ContractType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 16, 2016, 6:07:53 AM
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractTypeIndicator {

    private ContractType contractType;

    private int count;
}
