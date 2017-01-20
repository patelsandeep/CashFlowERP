package com.cashflow.project.frontend.controller.model;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.level.PerformanceStatus;
import com.cashflow.project.domain.project.level.SafetyRating;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
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
 * @since 23 Nov, 2016, 12:57:32 PM
 */
@Getter
@Setter
public class ProjectTaskModel implements Serializable {

    private static final long serialVersionUID = -821605556803194806L;

    @Nullable
    private String taskNumber;

    @Nullable
    private String milestoneId;

    @Nullable
    private String projectMilestone;

    @Nullable
    private String projectMilestoneName;

    @Nullable
    private String taskId;

    @Nullable
    private String taskUUID;

    @Nullable
    private String phaseId;

    @Nonnull
    private String customerName;

    @Nonnull
    private String projectUUID;

    @Nullable
    private String projectPhase;

    @Nullable
    private String projectPhaseName;

    @Nullable
    private String taskName;

    @Nullable
    private String customerId;

    @Nonnull
    private String projectName;

    @Nonnull
    private String projectId;

    @Nullable
    private SupervisorEntityModel taskSupervisor;

    @Nonnull
    private Calendar startDate;

    @Nonnull
    private Calendar endDate;

    @Nullable
    private String taskSummary;

    @Nullable
    private String taskDeliverable;

    private int taskCount = 0;

    private int teamMemberCount = 0;

    private int subContractorCount = 0;

    private int changeOrderCount = 0;

    @Nullable
    private BigDecimal poc = BigDecimal.ZERO;

    @Nullable
    private BigDecimal ppc = BigDecimal.ZERO;

    @Nullable
    private BigDecimal changeOrderAmount = BigDecimal.ZERO;

    @Nonnull
    private String projectManager;

    @Nullable
    private String taskSupervisorName;

    @Nonnull
    private SafetyRating safetyRating;

    @Nullable
    private PerformanceStatus performanceStatus;

    @Nullable
    private ProjectStatus taskStatus;

    @Nullable
    private Currency currency;

    @Nonnull
    private List<FileUrl> taskFileUrls = new ArrayList<>();

    @Nonnull
    private List<ProjectMilestone> projectMilestones = new ArrayList<>();

    @Nonnull
    private HashMap<String, InputStream> inputStreams = new HashMap<>();

    @Nullable
    private TaskBudgetInformation taskBudgetInformation;

    @Nullable
    private TaskRevenueInformation taskRevenueInformation;

    @Nullable
    private TaskDepositModel taskDepositModel;

    @Nullable
    private TaskPenaltyInformation taskPenaltyInformation;

}
