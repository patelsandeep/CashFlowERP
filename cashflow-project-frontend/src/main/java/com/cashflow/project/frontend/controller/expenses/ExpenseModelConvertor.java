package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.ProjectRole;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.ExpenseReportDataModel;
import com.cashflow.project.frontend.controller.model.ExpenseReportModel;
import com.cashflow.salestax.api.taxrate.TaxRateService;
import com.cashflow.salestax.domain.taxrate.TaxRate;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.service.api.AuthorizedUserService;
import com.anosym.common.Amount;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since 12 Dec, 2016, 2:57:47 PM
 */
@Dependent
public class ExpenseModelConvertor implements Serializable {

    private static final long serialVersionUID = 1179723366002730234L;

    @Inject
    private Logger logger;

    @Inject
    private AuthorizedUserService authorizedUserService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private TaxRateService taxRateService;

    @Inject
    private AsynchronousService asynchronousService;

    private Map<String, ProjectLevel<?>> levels;

    public void converExpenseDetailsToModel(@Nullable final ExpenseReport expenseReport,
                                            @Nonnull final ExpenseReportModel expenseReportModel) {

        if (expenseReport == null) {
            return;
        }
        expenseReportModel.setExpenseReportUUID(expenseReport.getUuid());
        expenseReportModel.setExpenseReportId(expenseReport.getExpenseReportId());
        expenseReportModel.setWeekStartDate(expenseReport.getWeekStartDate());
        expenseReportModel.setWeekEndingDate(expenseReport.getWeekEndDate());
        expenseReportModel.setExpenseReportStatus(expenseReport.getStatus());
        levels = new HashMap<>();
        setProjectLevels(expenseReport.getProjectLevel());
        expenseReportModel.setPhase("All");
        expenseReportModel.setMilestone("All");
        expenseReportModel.setTask("All");

        levels
                .entrySet()
                .forEach(level -> {
                    if (level.getValue() instanceof ProjectPhase) {
                        expenseReportModel.setPhaseName(((ProjectPhase) level.getValue()).getName());
                        expenseReportModel.setPhase(((ProjectPhase) level.getValue()).getUuid());
                    } else if (level.getValue() instanceof ProjectMilestone) {
                        expenseReportModel.setMilestoneName(((ProjectMilestone) level.getValue())
                                .getName());
                        expenseReportModel.setMilestone(((ProjectMilestone) level.getValue())
                                .getUuid());
                    } else if (level.getValue() instanceof ProjectTask) {
                        expenseReportModel.setTaskName(((ProjectTask) level.getValue()).getName());
                        expenseReportModel.setTask(((ProjectTask) level.getValue()).getUuid());
                        expenseReportModel.setTaskId(((ProjectTask) level.getValue()).getId());
                    }
                });
        if (null != expenseReport.getTeamMember()) {
            prepareTeamMemberValue(expenseReportModel, expenseReport.getTeamMember());
        } else {
            prepareSubContractorValue(expenseReportModel, expenseReport.getSubContractor());
        }
        setExpenseReportDetails(expenseReport, expenseReportModel);

    }

    @Nonnull
    public List<AccessRole> getAccessRoles() {
        final AccessRoleRequestContext requestContext = AccessRoleRequestContext
                .builder()
                .applicationCode("AZPM")
                .build();
        return accessRoleService.getAccessRoles(requestContext);
    }

    private void prepareTeamMemberValue(@Nonnull final ExpenseReportModel expenseReportModel,
                                        @Nonnull final TeamMember member) {

        expenseReportModel.setMember(member.getUuid());
        final AuthorizedUser user = authorizedUserService.findAuthorizedUser(member.getEmployeeUUID());
        expenseReportModel.setMemberName(user.getName());
        expenseReportModel.setMemberId(member.getMemberId());
        expenseReportModel.setMemberCategory(TeamMemberCategory.EMPLOYEE);
        final ProjectRole role = getProjectRole(member
                .getProjectRoles());
        expenseReportModel.setProjectRole(getProjectRoleName(role));
    }

    private void setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
    }

    private void prepareSubContractorValue(@Nonnull final ExpenseReportModel expenseReportModel,
                                           @Nonnull final SubContractor contractor) {

        expenseReportModel.setMember(contractor.getUuid());
        final Supplier supplier = supplierService.findSupplier(contractor.getSubContractorUUID());
        final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
        contactPersons.add(supplier.getContactPerson());
        contactPersons.stream()
                .filter((cp) -> (cp.getUuid().equals(contractor.getMemberName())))
                .forEachOrdered((cp) -> {
                    expenseReportModel.setMemberName(cp.getName());
                });
        expenseReportModel.setMemberId(contractor.getMemberId());
        expenseReportModel.setMemberCategory(TeamMemberCategory.SUB_CONTRACTOR);
        final ProjectRole role = getProjectRole(contractor
                .getProjectRoles());
        expenseReportModel.setProjectRole(getProjectRoleName(role));
    }

    @Nullable
    public ProjectRole getProjectRole(@Nonnull final Set<ProjectRole> projectRoles) {
        final ProjectRole projectRole = projectRoles
                .stream()
                .findFirst()
                .orElse(null);

        return projectRole;
    }

    @Nullable
    private String getProjectRoleName(@Nullable final ProjectRole role) {

        if (null == role) {
            return null;
        }

        final AccessRole ar = getAccessRoles().stream()
                .filter((accessRole) -> accessRole.getUuid().equals(role.getAccessScopeUUID()))
                .findFirst()
                .orElse(null);
        return Optional.ofNullable(ar)
                .map((accessRole) -> accessRole.getRoleName())
                .orElse(null);
    }

    private void setExpenseReportDetails(@Nonnull final ExpenseReport expenseReport,
                                         @Nonnull final ExpenseReportModel expenseReportModel) {
        expenseReportModel.setReportDataModels(new ArrayList<>());
        expenseReport.getExpenseDetails().stream()
                .map((expenseDetail) -> {
                    final ExpenseReportDataModel dataModel = new ExpenseReportDataModel();
                    dataModel.setBillable(expenseDetail.getBillable());
                    dataModel.setExpenseAmount(expenseDetail.getExpenseAmount().getValue());
                    dataModel.setExpenseDate(expenseDetail.getExpenseDate());
                    dataModel.setExpenseType(expenseDetail.getExpenseType());
                    dataModel.setTaxId(expenseDetail.getTaxId());
                    dataModel.setTaxId2(expenseDetail.getTaxId2());
                    dataModel.setProjectFileUrls(expenseDetail.getExpenseDocuments());
                    expenseReportModel.getReportDataModels().add(dataModel);
                    return dataModel;
                })
                .forEach((dataModel) -> {
                    calculateExpense(expenseReportModel, dataModel);
                });

    }

    public void updateExpense(@Nonnull final ExpenseReportModel expenseReportModel) {
        BigDecimal expenseAmount = BigDecimal.ZERO;
        Amount expenseTotalAmount = new Amount(expenseReportModel.getExpenseCurrency(), BigDecimal.ZERO);
        BigDecimal taxTotal = BigDecimal.ZERO;
        BigDecimal tax2Total = BigDecimal.ZERO;
        for (ExpenseReportDataModel erdm : expenseReportModel.getReportDataModels()) {
            BigDecimal expensePrice = BigDecimal.ZERO;
            expensePrice = expensePrice.add(erdm.getExpenseAmount());
            if (!isNullOrEmpty(erdm.getTaxId())) {
                final TaxRate tr = taxRateService.findTaxRate(erdm.getTaxId());
                taxTotal = taxTotal.add(expensePrice.multiply(tr.getRate().multiply(new BigDecimal(
                        "0.01"))));
            }
            if (!isNullOrEmpty(erdm.getTaxId2())) {
                final TaxRate tr = taxRateService.findTaxRate(erdm.getTaxId2());
                tax2Total = tax2Total.add(expensePrice.multiply(tr.getRate().multiply(new BigDecimal(
                        "0.01"))));
            }
            expenseAmount = expenseAmount.add(expensePrice);

        }
        final Amount exenseTotal = new Amount(expenseReportModel.getExpenseCurrency(), expenseAmount).scale(2);
        final Amount taxAmount = new Amount(expenseReportModel.getExpenseCurrency(), taxTotal).scale(2);
        final Amount tax2Amount = new Amount(expenseReportModel.getExpenseCurrency(), tax2Total).scale(2);
        expenseReportModel.setExpenseTotal(exenseTotal);
        expenseReportModel.setTaxTotal(taxAmount);
        expenseReportModel.setTax2Total(tax2Amount);
        expenseTotalAmount = expenseTotalAmount.add(exenseTotal.add(taxAmount).add(tax2Amount));
        expenseReportModel.setTotalExpenseAmount(expenseTotalAmount);
    }

    public void calculateExpense(@Nonnull final ExpenseReportModel expenseReportModel,
                                 @Nullable final ExpenseReportDataModel reportDataModel) {
        Amount expenseAmount = new Amount(expenseReportModel.getExpenseCurrency(), BigDecimal.ZERO);
        BigDecimal expensePrice = reportDataModel.getExpenseAmount();

        final TaxRate tr = Optional
                .ofNullable(reportDataModel.getTaxId())
                .map((taxId) -> taxRateService.findTaxRate(taxId))
                .orElse(null);
        final BigDecimal taxTotal = Optional
                .ofNullable(tr)
                .map((rate) -> expensePrice
                .multiply(new BigDecimal(0.01))
                .multiply(rate.getRate()).setScale(2, RoundingMode.HALF_DOWN))
                .orElse(BigDecimal.ZERO);

        final TaxRate tr2 = Optional
                .ofNullable(reportDataModel.getTaxId2())
                .map((taxId2) -> taxRateService.findTaxRate(taxId2))
                .orElse(null);
        BigDecimal tax2Total = Optional
                .ofNullable(tr2)
                .map((rate) -> expensePrice
                .multiply(new BigDecimal(0.01))
                .multiply(rate.getRate()).setScale(2, RoundingMode.HALF_DOWN))
                .orElse(BigDecimal.ZERO);

        final Amount exenseTotal = new Amount(expenseReportModel.getExpenseCurrency(), expensePrice).scale(2);
        final Amount taxAmount = new Amount(expenseReportModel.getExpenseCurrency(), taxTotal).scale(2);
        final Amount tax2Amount = new Amount(expenseReportModel.getExpenseCurrency(), tax2Total).scale(2);
        expenseAmount = expenseAmount.add(exenseTotal.add(taxAmount).add(tax2Amount));
        reportDataModel.setTotalAmount(expenseAmount);

        updateExpense(expenseReportModel);

    }
}
