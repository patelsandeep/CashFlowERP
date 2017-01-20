package com.cashflow.project.frontend.controller.businessunit;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.useraccount.domain.businessunit.Branch;
import com.cashflow.useraccount.domain.businessunit.BusinessDivision;
import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.cashflow.useraccount.service.api.businessunit.BusinessUnitService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 *
 * 
 * @since Oct 26, 2016, 12:46:57 PM
 */
@Named
@ViewScoped
public class BusinessUnitSelectionController implements Serializable {

    private static final long serialVersionUID = 3504545683792071478L;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private BusinessUnitService businessUnitService;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    @Inject
    @OnBusinessUnitSelected
    private Event<BusinessUnitEvent> onBusinessUnitEvent;

    @Getter
    @Setter
    private BusinessDivision selectedBusinessDivision;

    @Getter
    @Setter
    private Branch selectedBranch;

    @Getter
    @Setter
    private Department selectedDepartment;

    @Getter
    private SelectItem[] businessDivisions;

    @Getter
    private SelectItem[] branches;

    @Getter
    private SelectItem[] departments;

    @PostConstruct
    void initBusinessUnits() {
        final AuthorizedUser user = authorizedUser.get();
        final Future<EmployeeInformation> employeeInformationRequest
                = asynchronousService.execute(() -> {
                    return employeeInformationService.getEmployeeInformation(user.getUuid());
                });

        //Load business units
        loadBusinessUnits();

        try {
            //Load default business units
            setDefaultBusinessUnits(employeeInformationRequest.get());

            //Fire event on the default
            fireBusinessUnitSelected();
        } catch (final InterruptedException | ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    void onBusinessContextChanged(@Observes @OnBusinessContextChanged @Nonnull final BusinessUnitEvent businessUnitEvent) {
        checkNotNull(businessUnitEvent, "The businessUnitEvent must not be null");

        this.selectedBusinessDivision = businessUnitEvent.getSelectedBusinessDivision();
        this.selectedBranch = businessUnitEvent.getSelectedBranch();
        this.selectedDepartment = businessUnitEvent.getSelectedDepartment();
    }

    @Profile
    public void onBusinessUnitSelected() {
        fireBusinessUnitSelected();

        //Reload the business units with the selection
        loadBusinessUnits();
    }

    private void loadBusinessUnits() {
        final Future<List<BusinessUnit<?>>> businessDivisionsRequest
                = asynchronousService.execute(() -> loadBusinessDivisions());
        final Future<List<BusinessUnit<?>>> branchesRequest
                = asynchronousService.execute(() -> loadBranches());
        final Future<List<BusinessUnit<?>>> departmentsRequest
                = asynchronousService.execute(() -> loadDepartments());

        try {
            this.businessDivisions = toSelectItems(businessDivisionsRequest.get(), "(All Business Units)");
            this.branches = toSelectItems(branchesRequest.get(), "(All Branches)");
            this.departments = toSelectItems(departmentsRequest.get(), "(All Departments)");
        } catch (final InterruptedException | ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    private void fireBusinessUnitSelected() {
        final BusinessUnitEvent businessUnitEvent = BusinessUnitEvent
                .builder()
                .selectedBusinessDivision(selectedBusinessDivision)
                .selectedBranch(selectedBranch)
                .selectedDepartment(selectedDepartment)
                .build();
        this.onBusinessUnitEvent.fire(businessUnitEvent);
    }

    @Nonnull
    private List<BusinessUnit<?>> loadBusinessDivisions() {
        final String companyAccountUUID = companyAccount.get().getUuid();
        return businessUnitService
                .findBusinessUnits(companyAccountUUID)
                .stream()
                .filter((bu) -> bu instanceof BusinessDivision)
                .collect(Collectors.toList());
    }

    @Nonnull
    private List<BusinessUnit<?>> loadBranches() {
        final String branchParentBUUUID = selectedBusinessDivision != null
                ? selectedBusinessDivision.getUuid() : companyAccount.get().getUuid();
        return businessUnitService
                .findBusinessUnits(branchParentBUUUID)
                .stream()
                .filter((bu) -> bu instanceof Branch)
                .collect(Collectors.toList());
    }

    @Nonnull
    private List<BusinessUnit<?>> loadDepartments() {
        final String companyAccountUUID = companyAccount.get().getUuid();
        return businessUnitService
                .findBusinessUnits(companyAccountUUID)
                .stream()
                .filter((bu) -> bu instanceof Department)
                .collect(Collectors.toList());
    }

    @Nonnull
    private SelectItem[] toSelectItems(@Nonnull final List<BusinessUnit<?>> businessUnits,
                                       @Nonnull final String selectIndicationMessage) {
        return getSelectItems(businessUnits, true, selectIndicationMessage, (bu) -> bu.getName());
    }

    private void setDefaultBusinessUnits(@Nullable final EmployeeInformation employeeInformation) {
        final AuthorizedUser user = authorizedUser.get();
        final BusinessUnit<?> userBusinessUnit = user.getBusinessUnit();
        final BusinessUnit<?> userCompanyAccount = getUserCompanyAccount();
        if (!Objects.equals(userCompanyAccount, companyAccount.get())) {
            return;
        }

        if (userBusinessUnit instanceof Branch) {
            this.selectedBranch = (Branch) userBusinessUnit;

            final BusinessUnit<?> parentBusinessUnit = userBusinessUnit.getParentUnit();
            if (parentBusinessUnit instanceof BusinessDivision) {
                this.selectedBusinessDivision = (BusinessDivision) parentBusinessUnit;
            }
        } else if (userBusinessUnit instanceof BusinessDivision) {
            this.selectedBusinessDivision = (BusinessDivision) userBusinessUnit;
        }

        if (employeeInformation == null) {
            return;
        }

        this.selectedDepartment = employeeInformation.getDepartment();
    }

    @Nonnull
    private BusinessUnit<?> getUserCompanyAccount() {
        final AuthorizedUser user = authorizedUser.get();
        final BusinessUnit<?> userBusinessUnit = user.getBusinessUnit();
        if (userBusinessUnit instanceof CompanyAccount) {
            return userBusinessUnit;
        }

        if (userBusinessUnit instanceof BusinessDivision) {
            return checkNotNull(userBusinessUnit.getParentUnit());
        }

        if (userBusinessUnit instanceof Branch) {
            final BusinessUnit<?> parentBusinessUnit = checkNotNull(userBusinessUnit.getParentUnit());
            if (parentBusinessUnit instanceof CompanyAccount) {
                return parentBusinessUnit;
            }

            return checkNotNull(parentBusinessUnit.getParentUnit());
        }

        throw new IllegalStateException(format("Could not determine user company account: %s", user.getUuid()));
    }
}
