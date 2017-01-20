package com.cashflow.project.domain.service.people;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.domain.facade.people.ProjectSubContractorContextFacade;
import com.cashflow.project.domain.facade.people.ProjectSubContractorFacade;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.SubcontractorContext;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 26, 2016, 11:26:58 AM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ProjectSubContractorService.class)
public class ProjectSubContractorServiceImpl implements ProjectSubContractorService {

    @EJB
    private ProjectSubContractorFacade projectSubContractorFacade;

    @EJB
    private ProjectSubContractorContextFacade subContractorContextFacade;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Override
    public void save(@Nonnull final SubContractor subContractor) {
        checkNotNull(subContractor, "The subContractor must not be null");

        projectSubContractorFacade.edit(subContractor);
    }

    @Override

    public SubContractor getSubContractor(@Nonnull final String uuid) {
        checkNotNull(uuid, "The uuid must not be null");

        return projectSubContractorFacade.findBySubContractorIdOrUUID(uuid,
                                                                      businessAccount.get().getAccountId());
    }

    @Override
    @Nonnull
    public List<SubContractor> subContractors(@Nonnull final SubcontractorContext subcontractorContext) {
        checkNotNull(subcontractorContext, "The subcontractorContext must not be null");

        return subContractorContextFacade
                .find(subcontractorContext);
    }

}
