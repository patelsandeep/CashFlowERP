package com.cashflow.project.domain.service.equipmentinformation;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformation;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformationRequest;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformationResult;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformationService;
import com.cashflow.project.domain.expenseinformation.FilterProperty;
import com.cashflow.project.domain.facade.ownedequipment.OwnEquipmentContextFacade;
import com.cashflow.project.domain.facade.rentedequipment.RentedEquipmentExpenseContextFacade;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.project.domain.project.ownedequipment.EquipmentType;
import com.cashflow.project.domain.project.ownedequipment.OwnEquipmentContext;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipment;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
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
 * @since Dec 20, 2016, 6:42:59 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
public class EquipmentExpenseInformationServiceImpl implements EquipmentExpenseInformationService {

    private static final Map<String, Field> OWN_EQUIPMENT_CONTEXT_FIELD_MAPPING = ImmutableList
            .copyOf(OwnEquipmentContext.class.getDeclaredFields())
            .stream()
            .filter((field) -> field.isAnnotationPresent(FilterProperty.class))
            .collect(Collectors.toMap((field) -> {
                final FilterProperty fieldProperty = field.getAnnotation(FilterProperty.class);
                return firstNonNull(emptyToNull(fieldProperty.value()), field.getName());
            }, Function.identity()));

    private static final Map<String, Field> RENT_EQUIPMENT_CONTEXT_FIELD_MAPPING = ImmutableList
            .copyOf(RentedEquipment.class.getDeclaredFields())
            .stream()
            .filter((field) -> field.isAnnotationPresent(FilterProperty.class))
            .collect(Collectors.toMap((field) -> {
                final FilterProperty fieldProperty = field.getAnnotation(FilterProperty.class);
                return firstNonNull(emptyToNull(fieldProperty.value()), field.getName());
            }, Function.identity()));

    @EJB
    private OwnEquipmentContextFacade ownEquipmentContextFacade;

    @EJB
    private RentedEquipmentExpenseContextFacade rentedEquipmentContextFacade;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private EquipmentInfoBuilder equipmentInfoBuilder;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Inject
    private Logger logger;

    @Override
    @Nonnull
    @Profile
    public EquipmentExpenseInformationResult getEquipmentExpenseInformations(
            @Nonnull final EquipmentExpenseInformationRequest informationRequest) {
        checkNotNull(informationRequest, "The informationRequest must not be null");

        final String businessAccountId = checkNotNull(businessAccount.get().getAccountId());

        EquipmentType equipmentType = null;

        for (Map.Entry<Filter, Object> each : informationRequest.getFilters().entrySet()) {
            if (each.getKey().getAttribute().equals("equipmentType")) {
                equipmentType = (EquipmentType) each.getValue();
            }
        }

        final OwnEquipmentContext ownEquipmentContext = buildOwnEquipmentContext(informationRequest);

        final RentedEquipment rentedEquipmentContext = buildRentedEquipmentContext(informationRequest);

        Future<List<OwnedEquipmentExpense>> ownEquipmentRequest = null;
        Future<List<RentedEquipmentExpense>> rentedEquipmentRequest = null;
        Future<Integer> ownEquipmentCountRequest = null;
        Future<Integer> rentedEquipmentCountRequest = null;

        if (null == equipmentType || equipmentType == EquipmentType.OWNED) {
            ownEquipmentRequest = asynchronousService.execute(() -> {
                return ownEquipmentContextFacade
                        .find(businessAccountId, ownEquipmentContext);
            });
            ownEquipmentCountRequest = asynchronousService.execute(() -> {
                return ownEquipmentContextFacade.count(businessAccountId);
            });
        }

        if (null == equipmentType || equipmentType == EquipmentType.RENTED) {
            rentedEquipmentRequest = asynchronousService.execute(() -> {
                return rentedEquipmentContextFacade
                        .find(businessAccountId, rentedEquipmentContext);
            });

            rentedEquipmentCountRequest = asynchronousService.execute(() -> {
                return rentedEquipmentContextFacade.count(businessAccountId);
            });
        }

        try {
            int count = 0;
            if (null != rentedEquipmentCountRequest) {
                count = count + rentedEquipmentCountRequest.get();
            }
            if (null != ownEquipmentCountRequest) {
                count = count + ownEquipmentCountRequest.get();
            }
            List<OwnedEquipmentExpense> ownedEquipments = new ArrayList<>();
            List<RentedEquipmentExpense> rentedEquipments = new ArrayList<>();
            if (null != ownEquipmentRequest) {
                ownedEquipments = ownEquipmentRequest.get();
            }
            if (null != rentedEquipmentRequest) {
                rentedEquipments = rentedEquipmentRequest.get();
            }
            final Map<String, List<EquipmentExpenseInformation>> allOwnEquipments = ownedEquipments
                    .stream()
                    .collect(Collectors.toMap(OwnedEquipmentExpense::getUuid,
                                              (equipment) -> equipmentInfoBuilder.prepareOwnedInfos(equipment)));

            final List<EquipmentExpenseInformation> ownedInfos = allOwnEquipments
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            final List<EquipmentExpenseInformation> rentedInfos = rentedEquipments
                    .stream()
                    .map((equipment) -> equipmentInfoBuilder.prepareRentedInfo(equipment))
                    .collect(Collectors.toList());

            final List<EquipmentExpenseInformation> bothInfos = Stream
                    .concat(ownedInfos.stream(),
                            rentedInfos.stream())
                    .collect(Collectors.toList());

            return EquipmentExpenseInformationResult
                    .builder()
                    .count(count)
                    .equipmentExpenseInformations(bothInfos)
                    .build();

        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Nonnull
    private OwnEquipmentContext buildOwnEquipmentContext(
            @Nonnull final EquipmentExpenseInformationRequest informationRequest) {
        final OwnEquipmentContext ownEquipmentContext = new OwnEquipmentContext();
        informationRequest
                .getFilters()
                .forEach((filter, value) -> setOwnExpenseFilter(filter, value, ownEquipmentContext));

        return ownEquipmentContext
                .toBuilder()
                .build();
    }

    @Nonnull
    private RentedEquipment buildRentedEquipmentContext(
            @Nonnull final EquipmentExpenseInformationRequest informationRequest) {
        final RentedEquipment rentedEquipment = new RentedEquipment();
        informationRequest
                .getFilters()
                .forEach((filter, value) -> setRentedExpenseFilter(filter, value, rentedEquipment));

        return rentedEquipment
                .toBuilder()
                .build();
    }

    private void setOwnExpenseFilter(@Nonnull final Filter filter,
                                     @Nonnull final Object value,
                                     @Nonnull final Object context) {
        try {
            if (filter.getFilterDomains().stream().noneMatch(
                    (domain) -> domain == FilterDomain.EQUIPMENT_EXPENSE_INFORMATION)) {
                return;
            }

            final String filterId = filter.getAttribute();
            final Field field = OWN_EQUIPMENT_CONTEXT_FIELD_MAPPING.get(filterId);
            if (field == null) {
                logger.warning(format("Unrecognized filter id: %s for Filter Domain: Equipment", filterId));
                return;
            }

            field.setAccessible(true);
            field.set(context, value);
        } catch (final IllegalArgumentException | IllegalAccessException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private void setRentedExpenseFilter(@Nonnull final Filter filter,
                                        @Nonnull final Object value,
                                        @Nonnull final Object context) {
        try {
            if (filter.getFilterDomains().stream().noneMatch(
                    (domain) -> domain == FilterDomain.EQUIPMENT_EXPENSE_INFORMATION)) {
                return;
            }

            final String filterId = filter.getAttribute();
            final Field field = RENT_EQUIPMENT_CONTEXT_FIELD_MAPPING.get(filterId);
            if (field == null) {
                logger.warning(format("Unrecognized filter id: %s for Filter Domain: Equipment", filterId));
                return;
            }

            field.setAccessible(true);
            field.set(context, value);
        } catch (final IllegalArgumentException | IllegalAccessException ex) {
            throw Throwables.propagate(ex);
        }
    }

}
