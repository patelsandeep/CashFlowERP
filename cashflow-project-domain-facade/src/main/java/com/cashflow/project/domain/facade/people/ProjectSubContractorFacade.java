package com.cashflow.project.domain.facade.people;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.personnel.SubContractor;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 * @since Nov 26, 2016, 11:11:05 AM
 */
@Stateless
public class ProjectSubContractorFacade extends ProjectAbstractFacade<SubContractor> {

    public ProjectSubContractorFacade() {
        super(SubContractor.class);
    }

    @Nullable
    public SubContractor findBySubContractorIdOrUUID(@Nonnull final String subContractorIdOrUUID,
                                                     @Nonnull final String businessAccountId) {
        checkNotNull(subContractorIdOrUUID, "The subContractorIdOrUUID must not be null");

        final List<SubContractor> projectSubContractors = getEntityManager()
                .createNamedQuery("SubContractor.findBySubContractorIdOrUUID", SubContractor.class)
                .setParameter("id", subContractorIdOrUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(projectSubContractors.size() <= 1,
                   "Invalid lookup. subContractorIdOrUUID {%s} returns multiple results",
                   subContractorIdOrUUID);

        return projectSubContractors
                .stream()
                .findAny()
                .orElse(null);
    }
}
