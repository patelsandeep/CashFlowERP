package com.cashflow.project.api.projectinformation;

import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.indexing.service.Document;
import com.cashflow.indexing.service.Uuid;
import com.cashflow.project.domain.project.ContractType;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.invoice.Invoice;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.useraccount.domain.businessunit.Branch;
import com.cashflow.useraccount.domain.businessunit.BusinessDivision;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.anosym.common.Amount;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

/**
 *
 * 
 * @since Nov 12, 2016, 8:45:38 AM
 */
@Builder(toBuilder = true)
@Document(type = "project-information")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectInformation {

    @Uuid
    @Getter
    @Nonnull
    private String projectUUID;

    @Getter
    @Nonnull
    private String projectId;

    @Getter
    @Nonnull
    private String projectName;

    @Getter
    @Nullable
    private Customer customer;

    @Getter
    @Nullable
    private Department customerDepartment;

    @Getter
    @Nonnull
    private Calendar createdDate;

    @Getter
    @Nonnull
    private Calendar startDate;

    @Getter
    @Nonnull
    private Calendar endDate;

    @Nonnull
    private ProjectType projectType;

    @Nonnull
    private ContractType contractType;

    @Setter
    @Nonnull
    private String cityLocation;

    @Getter
    @Nonnull
    private Amount budgetAmount;

    @Getter
    @Nullable
    private Amount costLTD;

    @Getter
    @Nonnull
    private Budget budget;

    /**
     * We separate this for purposes of search. It is derived from the actual project budget.
     */
    @Nonnull
    private BigDecimal budgetedRevenue;

    @Getter
    @Nullable
    private List<Invoice> invoices;
    /**
     * We separate this for purposes of search. It is derived from totaling of all invoices under this project.
     */
    @Nullable
    private BigDecimal revenueInvoicedLTD;

    /**
     * We separate this for purposes of search. It is derived from total project completion.
     */
    @Nullable
    private BigDecimal revenueLTD;

    @Getter
    @Nullable
    private ProjectLevelProgress progress;

    @Nonnull
    private CompanyAccount companyAccount;

    @Nullable
    private BusinessDivision businessDivision;

    @Nullable
    private Branch branch;

    @Nullable
    private Department department;

    @Nullable
    private Employee projectAdmin;

    @Getter
    @Nonnull
    private Employee projectManager;

    @Nonnull
    @Singular
    private List<String> businessUnitUUIDs;
}
