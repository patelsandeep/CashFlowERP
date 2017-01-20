package com.cashflow.project.api.people;

import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.SubcontractorContext;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since Nov 26, 2016, 11:08:42 AM
 */
public interface ProjectSubContractorService {

    void save(@Nonnull final SubContractor subContractor);

    @Nullable
    SubContractor getSubContractor(@Nonnull final String uuid);

    @Nonnull
    List<SubContractor> subContractors(@Nonnull final SubcontractorContext subcontractorContext);

}
