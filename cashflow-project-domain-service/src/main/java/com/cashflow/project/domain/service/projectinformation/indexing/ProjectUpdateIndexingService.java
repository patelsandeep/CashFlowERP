package com.cashflow.project.domain.service.projectinformation.indexing;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.entitydomains.facade.OnCreateOrUpdate;
import com.cashflow.indexing.service.processor.DocumentEvent;
import com.cashflow.indexing.service.processor.OnDocumentChanged;
import com.cashflow.project.api.projectinformation.ProjectInformation;
import com.cashflow.project.api.projectinformation.ProjectInformationRequest;
import com.cashflow.project.api.projectinformation.ProjectInformationService;
import com.cashflow.project.config.indexing.ProjectIndexingConfigurationService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.project.domain.service.DatabaseContext;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.AllArgsConstructor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 14, 2016, 9:05:48 PM
 */
@Startup
@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ProjectUpdateIndexingService {

    private static final Class<?> ABSTRACT_BUSINESS_DOMAIN = AbstractBusinessAccountDomain.class;

    @Inject
    private ProjectIndexingConfigurationService projectIndexingConfigurationService;

    @Inject
    @DatabaseContext
    private ProjectInformationService projectInformationService;

    @Inject
    @OnDocumentChanged
    private Event<DocumentEvent<ProjectInformation>> onDocumentEvented;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    public void onProjectUpdated(
            @Observes(during = TransactionPhase.AFTER_SUCCESS) @OnCreateOrUpdate @Nonnull final AbstractBusinessAccountDomain anyProjectDomain) {
        checkNotNull(anyProjectDomain, "The project must not be null");

        if (!projectIndexingConfigurationService.isIndexingEnabled()) {
            return;
        }

        final Project project = getProject(anyProjectDomain);
        if (project != null) {
            indexProject(project);
        }
    }

    @Nullable
    @VisibleForTesting
    Project getProject(@Nonnull final AbstractBusinessAccountDomain anyProjectDomain) {
        if (anyProjectDomain instanceof Project) {
            return (Project) anyProjectDomain;
        }

        final List<FieldValue> fieldValues = getDeclaredFields(anyProjectDomain, anyProjectDomain.getClass());
        return fieldValues
                .stream()
                .filter((fieldValue) -> fieldValue.field.getType() == Project.class)
                .findFirst()
                .map((fieldValue) -> ((Project) fieldValue.value))
                .orElse(null);
    }

    @Nonnull
    private List<FieldValue> getDeclaredFields(@Nonnull final Object anyProjectDomain,
                                               @Nonnull final Class<?> anyProjectDomainClass) {
        final ImmutableList.Builder<FieldValue> builder = ImmutableList.builder();
        builder.addAll(ImmutableList
                .copyOf(anyProjectDomainClass.getDeclaredFields())
                .stream()
                .map((field) -> getFieldValue(anyProjectDomain, field))
                .filter((fieldValue) -> fieldValue.value != null)
                .filter((fieldValue) -> ABSTRACT_BUSINESS_DOMAIN.isAssignableFrom(fieldValue.field.getType()))
                .collect(Collectors.toList()));

        final Class<?> superClass = anyProjectDomainClass.getSuperclass();
        if (superClass != AbstractBusinessAccountDomain.class) {
            builder.addAll(getDeclaredFields(anyProjectDomain, superClass));
        }

        return builder
                .build()
                .stream()
                .flatMap(this::getFieldValues)
                .collect(Collectors.toList());
    }

    @Nonnull
    private Stream<FieldValue> getFieldValues(@Nonnull final FieldValue fieldValue) {
        final ImmutableList.Builder<FieldValue> builder = ImmutableList.builder();
        builder.add(fieldValue);

        return builder
                .addAll(getDeclaredFields(fieldValue.value, fieldValue.field.getType()))
                .build()
                .stream();
    }

    @Nonnull
    private FieldValue getFieldValue(@Nonnull final Object anyProjectDomain, @Nonnull final Field field) {
        try {
            field.setAccessible(true);
            return new FieldValue(field, field.get(anyProjectDomain));
        } catch (final IllegalArgumentException | IllegalAccessException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private void indexProject(@Nonnull final Project project) {
        checkNotNull(project, "The project must not be null");

        final String projectUUID = project.getUuid();
        final Filter filter = Filter
                .builder()
                .attribute("uuid")
                .filterDomain(FilterDomain.PROJECT)
                .name("Project")
                .build();
        final ProjectInformationRequest projectInformationRequest = ProjectInformationRequest
                .builder()
                .businessUnitUUID(project.getBusinessUnitUUID())
                .departmentUUID(project.getDepartmentUUID())
                .filter(filter, projectUUID)
                .build();

        final List<ProjectInformation> projectInformations = projectInformationService
                .getProjectInformations(projectInformationRequest)
                .getProjectInformations();

        final String index = checkNotNull(businessAccount.get().getAccountId());
        final DocumentEvent<ProjectInformation> documentEvent = DocumentEvent
                .<ProjectInformation>builder()
                .index(index)
                .documents(projectInformations)
                .build();

        onDocumentEvented.fire(documentEvent);
    }

    @AllArgsConstructor
    private static final class FieldValue {

        private final Field field;

        private final Object value;
    }
}
