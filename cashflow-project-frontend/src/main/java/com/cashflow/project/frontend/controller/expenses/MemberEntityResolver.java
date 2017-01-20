package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.domain.supplier.SupplierSearchContext;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.SubcontractorContext;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.personnel.TeamMemberContext;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.service.api.search.AuthorizedUserSearchService;
import com.cashflow.useraccount.service.api.search.SearchContext;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * 
 * @since 6 Dec, 2016, 12:49:47 PM
 */
@Dependent
public class MemberEntityResolver implements Serializable {

    private static final long serialVersionUID = 5264454674103317466L;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectTeamMemberService projectTeamMemberService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private AuthorizedUserSearchService authorizedUserSearchService;

    @Inject
    private ProjectSubContractorService projectSubContractorService;

    @Nonnull
    @Profile
    @RequestCached
    public List<MemberEntityModel> loadMemberValues(@Nonnull final String projectLevelUUID) {

        final Future<List<MemberEntityModel>> members = asynchronousService.execute(() -> {

            final TeamMemberContext memberContext = TeamMemberContext
                    .builder()
                    .projectLevelUUID(projectLevelUUID)
                    .build();
            final List<TeamMember> teamMembers = projectTeamMemberService.teamMembers(memberContext);
            final List<String> employeesUUIDs = teamMembers
                    .stream()
                    .map((member) -> member.getEmployeeUUID())
                    .collect(Collectors.toList());

            final SearchContext searchContext = SearchContext.builder()
                    .userUUIDs(employeesUUIDs)
                    .offset(0)
                    .limit(100)
                    .build();

            final List<AuthorizedUser> employess = authorizedUserSearchService.search(searchContext);
            final List<MemberEntityModel> modelList = new ArrayList<>();

            teamMembers.forEach((teamMember) -> {
                employess.stream()
                        .filter((authorizedUser) -> (authorizedUser.getUuid().equals(teamMember.getEmployeeUUID())))
                        .forEach((authorizedUser) -> {
                            modelList.add(new MemberEntityModel(teamMember.getUuid(), authorizedUser.getName(),
                                                                TeamMemberCategory.EMPLOYEE));
                        });
            });

            return modelList;
        });
        final Future<List<MemberEntityModel>> subContractors = asynchronousService.execute(() -> {
            final SubcontractorContext sc = SubcontractorContext.
                    builder()
                    .projectLevelUUID(projectLevelUUID)
                    .build();

            final List<SubContractor> contractors = projectSubContractorService.subContractors(sc);

            final List<String> supplierUUIDs = contractors
                    .stream()
                    .map((sub) -> sub.getSubContractorUUID())
                    .collect(Collectors.toList());
            final SupplierSearchContext supplierContext = SupplierSearchContext.builder()
                    .supplierUUIDs(supplierUUIDs)
                    .offset(0)
                    .limit(100)
                    .build();

            final List<Supplier> suppliers = supplierService.findSuppliers(supplierContext);

            final List<MemberEntityModel> modelList = new ArrayList<>();

            for (SubContractor contractor : contractors) {
                for (Supplier supplier : suppliers) {
                    if (supplier.getUuid().equals(contractor.getSubContractorUUID())) {
                        final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
                        contactPersons.add(supplier.getContactPerson());
                        for (ContactPerson contactPerson : contactPersons) {
                            if (contractor.getMemberName().equals(contactPerson.getUuid())) {
                                modelList.add(new MemberEntityModel(contractor.getUuid(), contactPerson.getName(),
                                                                    TeamMemberCategory.SUB_CONTRACTOR));
                            }
                        }

                    }
                }
            }

            return modelList;

        });

        try {
            return ImmutableList
                    .<MemberEntityModel>builder()
                    .addAll(members.get())
                    .addAll(subContractors.get())
                    .build();
        } catch (final InterruptedException | ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }
}
