package com.cashflow.project.frontend.controller.businessunit;

import com.cashflow.useraccount.domain.businessunit.Branch;
import com.cashflow.useraccount.domain.businessunit.BusinessDivision;
import com.cashflow.useraccount.domain.businessunit.Department;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * 
 * @since Oct 26, 2016, 5:33:01 PM
 */
@Getter
@Builder
public class BusinessUnitEvent {

    @Nullable
    private final BusinessDivision selectedBusinessDivision;

    @Nullable
    private final Branch selectedBranch;

    @Nullable
    private final Department selectedDepartment;

}
