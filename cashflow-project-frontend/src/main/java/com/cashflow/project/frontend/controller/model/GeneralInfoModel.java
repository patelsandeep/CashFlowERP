package com.cashflow.project.frontend.controller.model;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class GeneralInfoModel {

    private static final long serialVersionUID = 6302789861441084512L;

    @Nullable
    private String projectSummary;

    @Nullable
    private String location;

    @Nullable
    private String locationPermission;

    @Nullable
    private String projectInsurance;

    @Nullable
    private String customerDepositReq;

    @Nullable
    private BigDecimal customerDeposit = BigDecimal.ZERO;

    @Nullable
    private ProjectLevelCategory levelCategory;

    @Nonnull
    private List<FileUrl> projectFileUrls = new ArrayList<>();

    @Nonnull
    private HashMap<String, InputStream> inputStreams = new HashMap<>();

    private boolean includePhases = true;

    private boolean includeMilestones = true;

    private boolean includeTasks = true;

    private boolean includeSubContractors = true;

    private boolean includeCustomerDeposit = true;

}
