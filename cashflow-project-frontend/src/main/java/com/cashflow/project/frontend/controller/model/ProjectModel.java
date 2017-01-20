package com.cashflow.project.frontend.controller.model;

import com.cashflow.project.domain.project.ContractType;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.RevenueRecordingMethod;
import com.cashflow.project.frontend.controller.project.ProjectTypeEntityModel;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.anosym.common.Currency;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 */
@Getter
@Setter
public class ProjectModel implements Serializable {

    private static final long serialVersionUID = 1228103611055314486L;

    @Nonnull
    private String projectName;

    @Nonnull
    private String projectId;

    @Nonnull
    private ProjectType projectType;

    @Nullable
    private String customerOrDeptUUID;

    @Nullable
    private String projectUUID;

    @Nullable
    private ProjectTypeEntityModel projectTypeEntityModel;

    @Nullable
    private String customerId;

    @Nullable
    private String departmentId;

    @Nonnull
    private Date startDate;

    @Nonnull
    private Date endDate;

    @Nullable
    private ContractType contractType;

    @Nullable
    private RevenueRecordingMethod revenueRecMethod;

    @Nullable
    private Currency currency;

    @Nullable
    private BigDecimal projectBudgedCost = BigDecimal.ZERO;

    @Nullable
    private BigDecimal projectRevenue = BigDecimal.ZERO;

    @Nullable
    private BigDecimal projectGrossProfit = BigDecimal.ZERO;

    @Nullable
    private Employee projectManager;

    @Nonnull
    private ProjectStatus projectStatus;

    @Nonnull
    private List<Employee> projectManagers = new ArrayList<>();

}
