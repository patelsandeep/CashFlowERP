package com.cashflow.project.domain.approval;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.EnumType.STRING;

/**
 *
 * 
 * @since Nov 21, 2016, 4:07:10 PM
 */
@Entity
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ProjectApproval extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -9209443466640896577L;

    @Nonnull
    private String approverUUID;

    @Nonnull
    @Enumerated(STRING)
    private ProjectLevelCategory projectLevelCategory;

    @Nullable
    @ElementCollection
    @Enumerated(STRING)
    private List<ApprovalType> approvalTypes;

    @Nonnull
    @OneToOne
    private ProjectLevel<?> projectLevel;

    @Nonnull
    public List<ApprovalType> getApprovalTypes() {
        return approvalTypes == null ? new ArrayList<>() : approvalTypes;
    }

    public void setApprovalTypes(@Nonnull final List<ApprovalType> approvalTypes) {
        checkNotNull(approvalTypes, "The approvalTypes must not be null");

        getApprovalTypes().clear();
        getApprovalTypes().addAll(approvalTypes);
    }

}
