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
 * @since 21 Nov, 2016, 7:36:11 PM
 */
@Getter
@Setter
public class ProjectMilestoneModel implements Serializable {

    private static final long serialVersionUID = 6778868385370411733L;

    @Nullable
    private String milestoneNumber;

    @Nullable
    private String milestoneId;

    @Nullable
    private String phaseId;

    @Nonnull
    private String customerName;

    @Nonnull
    private String projectUUID;

    @Nullable
    private String projectPhase;

    @Nullable
    private String milestoneName;

    @Nullable
    private String customerId;

    @Nonnull
    private String projectName;

    @Nonnull
    private String projectId;

    @Nullable
    private SupervisorEntityModel milestoneSupervisor;

    @Nonnull
    private Calendar startDate;

    @Nonnull
    private Calendar endDate;

    @Nullable
    private String milestoneSummary;

    @Nullable
    private String milestoneDeliverable;

    @Nullable
    private BigDecimal milestoneBudgetCost = BigDecimal.ZERO;

    @Nullable
    private Currency currency;

    @Nullable
    private BigDecimal milestoneBudgetRevenue = BigDecimal.ZERO;

    @Nullable
    private BigDecimal milestoneBudgetGrossProfit = BigDecimal.ZERO;

    @Nullable
    private BigDecimal depositOrRetainers = BigDecimal.ZERO;

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
    private ProjectStatus milestoneStatus;

    @Nonnull
    private List<FileUrl> milestoneFileUrls = new ArrayList<>();

    @Nonnull
    private HashMap<String, InputStream> inputStreams = new HashMap<>();

}
