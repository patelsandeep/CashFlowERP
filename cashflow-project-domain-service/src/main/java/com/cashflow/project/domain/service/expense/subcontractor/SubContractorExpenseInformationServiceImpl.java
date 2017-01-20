package com.cashflow.project.domain.service.expense.subcontractor;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.domain.supplier.SupplierSearchContext;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseInformation;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseInformation.SubContractorExpenseInformationBuilder;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseInformationRequest;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseInformationResult;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseInformationService;
import com.cashflow.project.domain.expenseinformation.FilterProperty;
import com.cashflow.project.domain.facade.expense.subcontractor.SubContractorExpenseContextFacade;
import com.cashflow.project.domain.project.expense.subcontractor.Categorization;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpenseContext;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.anosym.common.Amount;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.lang.String.format;

/**
 *
 * 
 * @since Jan 3, 2017, 3:00:24 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
public class SubContractorExpenseInformationServiceImpl implements SubContractorExpenseInformationService {

    private static final Map<String, Field> SUBCONTRACTOR_EXPENSE_CONTEXT_FIELD_MAPPING = ImmutableList
            .copyOf(SubContractorExpenseContext.class.getDeclaredFields())
            .stream()
            .filter((field) -> field.isAnnotationPresent(FilterProperty.class))
            .collect(Collectors.toMap((field) -> {
                final FilterProperty fieldProperty = field.getAnnotation(FilterProperty.class);
                return firstNonNull(emptyToNull(fieldProperty.value()), field.getName());
            }, Function.identity()));

    @Inject
    private SubContractorExpenseContextFacade subContractorExpenseContextFacade;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private SupplierService supplierService;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Inject
    private Logger logger;

    @Override
    @Nonnull
    public SubContractorExpenseInformationResult getSubContractorExpenseInformations(
            @Nonnull final SubContractorExpenseInformationRequest informationRequest) {

        checkNotNull(informationRequest, "The informationRequest must not be null");

        final String businessAccountId = checkNotNull(businessAccount.get().getAccountId());

        final SubContractorExpenseContext subContractorExpenseContext = buildSubContractorExpenseContext(
                informationRequest);

        final Future<List<SubContractorExpense>> expensesRequest = asynchronousService
                .execute(() -> subContractorExpenseContextFacade
                        .find(businessAccountId, subContractorExpenseContext));
        //adding context in count request. So that we can get count based on search parameters
        final Future<Integer> expensesCount = asynchronousService
                .execute(() -> subContractorExpenseContextFacade
                        .count(businessAccountId, subContractorExpenseContext));

        try {

            final List<SubContractorExpense> expenses = expensesRequest.get();

            final Map<String, Supplier> suppliers = prepareSuppliers(expenses);

            final List<SubContractorExpenseInformation> allExpenses = expenses
                    .stream()
                    .map((expense) -> prepareExpenses(expense, suppliers))
                    .collect(Collectors.toList());

            return SubContractorExpenseInformationResult
                    .builder()
                    .count(expensesCount.get())
                    .subContractorExpenseInformations(allExpenses)
                    .build();
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

    }

    @Nonnull
    private SubContractorExpenseContext buildSubContractorExpenseContext(
            @Nonnull final SubContractorExpenseInformationRequest informationRequest) {
        final SubContractorExpenseContext contractorExpenseContext = new SubContractorExpenseContext();
        informationRequest
                .getFilters()
                .forEach((filter, value) -> setContractorExpenseContext(filter, value, contractorExpenseContext));

        return contractorExpenseContext
                .toBuilder()
                .build();
    }

    private void setContractorExpenseContext(@Nonnull final Filter filter,
                                             @Nonnull final Object value,
                                             @Nonnull final Object context) {
        try {
            if (filter.getFilterDomains().stream().noneMatch(
                    (domain) -> domain == FilterDomain.SUB_CONTRACTOR_EXPENSE_INFORMATION)) {
                return;
            }

            final String filterId = filter.getAttribute();
            final Field field = SUBCONTRACTOR_EXPENSE_CONTEXT_FIELD_MAPPING.get(filterId);
            if (field == null) {
                logger.warning(format("Unrecognized filter id: %s for Filter Domain: SubContractorExpense", filterId));
                return;
            }

            field.setAccessible(true);
            field.set(context, value);
        } catch (final IllegalArgumentException | IllegalAccessException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Nonnull
    private SubContractorExpenseInformation prepareExpenses(
            @Nonnull final SubContractorExpense expense,
            @Nonnull final Map<String, Supplier> suppliers) {

        SubContractorExpenseInformationBuilder info = SubContractorExpenseInformation
                .builder();

        info.expenseStatus(expense.getExpenseStatus());
        info.expenseUuid(expense.getUuid());

        setProject(expense.getProjectLevel(), info);

        final Supplier supplier = suppliers.get(expense.getSupplierUUID());

        info.subContractorName(supplier.getName());
        info.supplierID(supplier.getSupplierNumber());

        info.invoiceDate(expense.getInvoiceDueDate());
        info.invoiceNumber(expense.getInvoiceNumber());

        info = preapreCostValues(expense, info);

        return info.build();

    }

    @Nullable
    private void setProject(@Nullable final ProjectLevel level,
                            @Nonnull final SubContractorExpenseInformationBuilder info) {
        if (level != null && level.getParentLevel() == null) {
            info.projectId(level.getId());
            return;
        }
        setProject(level.getParentLevel(), info);
    }

    @Nonnull
    private Map<String, Supplier> prepareSuppliers(@Nonnull final List<SubContractorExpense> expenses) {

        final List<String> supplierUUIDs = expenses
                .stream()
                .map((expense) -> expense.getSupplierUUID())
                .collect(Collectors.toList());

        final SupplierSearchContext searchContext = SupplierSearchContext
                .builder()
                .supplierUUIDs(supplierUUIDs)
                .build();
        final Map<String, Supplier> supplierRequests = supplierService
                .findSuppliers(searchContext)
                .stream()
                .collect(Collectors.toMap(Supplier::getUuid, Function.identity()));

        return supplierRequests;

    }

    @Nonnull
    private SubContractorExpenseInformationBuilder preapreCostValues(@Nonnull final SubContractorExpense expense,
                                                                     @Nonnull final SubContractorExpenseInformationBuilder info) {

        final Categorization categorization = expense.getCategorization();

        info.costAmount(categorization.getEquipmentExpense()
                .add(categorization.getMaterialExpense())
                .add(categorization.getLabourExpense()));

        final Amount labourAmount = categorization.getLabourExpense()
                .subtract(categorization.getLabourNonBillable());

        final Amount labourMarkUp = labourAmount
                .multiply(0.01)
                .multiply(categorization.getLabourMarkUp()).scale(2);

        final Amount labourBillable = labourMarkUp.add(labourAmount);

        final Amount materialAmount = categorization.getMaterialExpense()
                .subtract(categorization.getMaterialNonBillable());

        final Amount materialMarkUp = materialAmount
                .multiply(0.01)
                .multiply(categorization.getMaterialMarkUp()).scale(2);

        final Amount materialBillable = materialMarkUp.add(materialAmount);

        final Amount equipmentAmount = categorization.getEquipmentExpense()
                .subtract(categorization.getEquipmentNonBillable());

        final Amount equipmentMarkUp = equipmentAmount
                .multiply(0.01)
                .multiply(categorization.getEquipmentMarkUp()).scale(2);

        final Amount equipmentBillable = equipmentMarkUp.add(equipmentAmount);

        info.markUpAmount(equipmentMarkUp.add(materialMarkUp).add(labourMarkUp));

        info.billableAmount(labourBillable.add(materialBillable).add(equipmentBillable));

        return info;
    }
}
