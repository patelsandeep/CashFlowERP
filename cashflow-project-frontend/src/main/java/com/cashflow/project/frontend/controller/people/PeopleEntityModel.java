package com.cashflow.project.frontend.controller.people;

import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.personnel.CostRateSource;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.FunctionalRole;
import com.anosym.common.Amount;
import java.math.BigDecimal;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since 29 Nov, 2016, 12:27:43 PM
 */
@Getter
@Setter
public class PeopleEntityModel {

    @Nullable
    private String projectId;

    @Nullable
    private String phaseId;

    @Nullable
    private String milestoneId;

    @Nullable
    private String taskId;

    @Nullable
    private String teamMember;

    @Nullable
    private String memberId;

    @Nullable
    private FunctionalRole platformRole;

    @Nullable
    private Department department;

    @Nullable
    private String projectRole;

    @Nullable
    private CostRateSource costRateSource;

    @Nullable
    private MarkUpMethod markupMethod;

    @Nullable
    private BigDecimal markup;

    @Nullable
    private Amount costRate;

    @Nullable
    private Amount billingRate;

    @Nullable
    private String memberUUID;

    @Nullable
    private TeamMemberCategory category;

}
