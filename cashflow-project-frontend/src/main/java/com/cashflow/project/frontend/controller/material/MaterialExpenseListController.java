package com.cashflow.project.frontend.controller.material;

import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.pagination.PaginationModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.anosym.common.Amount;
import com.anosym.common.Currency;
import com.anosym.profiler.Profile;
import com.anosym.urlbuilder.QueryParameter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MATERIAL_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 */
@ModelViewScopedController
public class MaterialExpenseListController extends PaginationModel implements Serializable {

    private static final String PROJECT_MATERIAL_DETAIL = "material/material-detail.xhtml";

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private List<MaterialExpenseModel> materialExpenses;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    private ProjectService projectService;

    @Getter
    @Setter
    @Nullable
    private String searchValue;

    @Getter
    @Setter
    @Nullable
    private String filterValue;

    @Getter
    @Setter
    @Nullable
    private String projectLevelUUID;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @PostConstruct
    public void initMaterialExpenses() {
        final String projectUUID = checkNotNull(pUUID.get(), "Project is not selected");
        project = checkNotNull(projectService.getProject(projectUUID), "Project not found");
        projectLevelUUID = project.getUuid();
        loadMaterialExpenses();
        this.setCount(materialExpenses.size());
    }

    @Override
    public void loadData() {
        initMaterialExpenses();
    }

    public List<ProjectPhase> getLoadProjectPhases() {
        return projectPhaseService
                .getProjectPhases(PhaseContext
                        .builder()
                        .projectUUID(project.getUuid())
                        .build());
    }

    public List<ProjectMilestone> getLoadProjectMilestones() {
        return projectMilestoneService
                .getMilestones(MilestoneContext.builder()
                        .parentLevelUUID(project.getUuid())
                        .build());
    }

    public List<ProjectTask> getLoadProjectTasks() {
        return projectTaskService
                .getTasks(TaskContext.builder()
                        .parentLevelUUID(project.getUuid())
                        .build());
    }

    @Profile
    public List<MaterialExpenseModel> loadMaterialExpenses() {
        materialExpenses = new ArrayList<>();
        // There are no material expenses to load now => Set one dummy data for the test
        MaterialExpenseModel materialExpense1 = new MaterialExpenseModel();
        materialExpense1.setAmount(new Amount(Currency.CRC, BigDecimal.ONE));
        materialExpense1.setBillRate(new Amount(Currency.CRC, BigDecimal.ONE));
        materialExpense1.setCostRate(new Amount(Currency.CRC, BigDecimal.ONE));
        materialExpense1.setInventory("YES");
        materialExpense1.setMaterialId("M1234");
        materialExpense1.setMaterialName("Sand");
        materialExpense1.setMilestoneId("MIL1234");
        materialExpense1.setPhaseId("PHASE1234");
        materialExpense1.setTaskId("T12345");
        materialExpense1.setProjectId("P12020202");
        materialExpense1.setUnits(20);

        MaterialExpenseModel materialExpense2 = new MaterialExpenseModel();
        materialExpense2.setAmount(new Amount(Currency.CRC, BigDecimal.ONE));
        materialExpense2.setBillRate(new Amount(Currency.CRC, BigDecimal.ONE));
        materialExpense2.setCostRate(new Amount(Currency.CRC, BigDecimal.ONE));
        materialExpense2.setInventory("NO");
        materialExpense2.setMaterialId("M1234");
        materialExpense2.setMaterialName("Cement");
        materialExpense2.setMilestoneId("MIL1234");
        materialExpense2.setTaskId("T12345");
        materialExpense2.setPhaseId("PHASE1234");
        materialExpense2.setProjectId("P12020202");
        materialExpense2.setUnits(20);

        materialExpenses.add(materialExpense1);
        materialExpenses.add(materialExpense2);
        return materialExpenses;
    }

    public void loadDefaultValues() {
        projectLevelUUID = project.getUuid();
    }

    public void viewOrEditProjectMaterialExpense(@Nonnull final String materialUUID) throws IOException {

        final String redirectUrl = generateMaterialDetailEditLink(materialUUID);

        final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.getFlash().setKeepMessages(true);
        externalContext.redirect(redirectUrl);
    }

    public void addNewMaterialExpense() throws IOException {

        final String redirectUrl = generateMaterialDetailAddLink();

        final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.getFlash().setKeepMessages(true);
        externalContext.redirect(redirectUrl);
    }

    private String generateMaterialDetailEditLink(final String materialUUID) {

        final QueryParameter<String> projectUUIDParam = QueryParameter
                .<String>builder()
                .parameter(SELECTED_PROJECT_UUID)
                .parameterValue(project.getUuid())
                .parameter(SELECTED_MATERIAL_UUID)
                .parameterValue(materialUUID)
                .build();
        final UrlContext urlContext = UrlContext
                .builder()
                .additionalParameter(projectUUIDParam)
                .forceFacesRedirect(true)
                .path(PROJECT_MATERIAL_DETAIL)
                .build();
        return staticLinkUrlBuilder.buildURL(urlContext);
    }

    private String generateMaterialDetailAddLink() {
        final QueryParameter<String> projectUUIDParam = QueryParameter
                .<String>builder()
                .parameter(SELECTED_PROJECT_UUID)
                .parameterValue(project.getUuid())
                .build();
        final UrlContext urlContext = UrlContext
                .builder()
                .additionalParameter(projectUUIDParam)
                .forceFacesRedirect(true)
                .path(PROJECT_MATERIAL_DETAIL)
                .build();
        return staticLinkUrlBuilder.buildURL(urlContext);
    }

}
