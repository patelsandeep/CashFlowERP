package com.cashflow.project.frontend.controller.model;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.level.PerformanceStatus;
import com.cashflow.project.domain.project.level.SafetyRating;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.anosym.common.Currency;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
public class ProjectPhaseModel implements Serializable {

    private static final long serialVersionUID = 8861493922807499266L;

    @Nullable
    private String phaseNumber;

    @Nullable
    private String phaseId;

    @Nonnull
    private String customerName;

    @Nonnull
    private String projectUUID;

    @Nullable
    private String phaseName;

    @Nullable
    private String customerId;

    @Nonnull
    private String projectName;

    @Nonnull
    private String projectId;

    @Nullable
    private SupervisorEntityModel phaseSupervisor;

    @Nonnull
    private Calendar startDate;

    @Nonnull
    private Calendar endDate;

    @Nullable
    private String phaseSummary;

    @Nullable
    private String phaseDeliverable;

    @Nullable
    private BigDecimal phaseBudgetCost = BigDecimal.ZERO;

    @Nullable
    private Currency currency;

    @Nullable
    private BigDecimal phaseBudgetRevenue = BigDecimal.ZERO;

    @Nullable
    private BigDecimal phaseBudgetGrossProfit = BigDecimal.ZERO;

    @Nullable
    private BigDecimal depositOrRetainers = BigDecimal.ZERO;

    private int milestoneCount = 0;

    private int taskCount = 0;

    private int teamMemberCount = 0;

    private int subContractorCount = 0;

    private int changeOrderCount = 0;

    private int penaltyCount = 0;

    @Nullable
    private BigDecimal poc = BigDecimal.ZERO;

    @Nullable
    private BigDecimal ppc = BigDecimal.ZERO;

    @Nullable
    private BigDecimal changeOrderAmount = BigDecimal.ZERO;

    @Nullable
    private BigDecimal penaltiesAmount = BigDecimal.ZERO;

    @Nonnull
    private String projectManager;

    @Nonnull
    private SafetyRating safetyRating;

    @Nullable
    private PerformanceStatus performanceStatus;

    @Nullable
    private ProjectStatus phaseStatus;

    @Nonnull
    private List<FileUrl> phaseFileUrls = new ArrayList<>();

    @Nonnull
    private HashMap<String, InputStream> inputStreams = new HashMap<>();

}
