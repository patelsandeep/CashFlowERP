package com.cashflow.project.frontend.controller.phase;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.progress.ProjectLevelProgressService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
import com.cashflow.project.frontend.controller.pagination.PaginationModel;
import com.cashflow.project.frontend.controller.strategies.LevelCategoryChildStrategyBuilder;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 17, 2016, 10:50:25 PM
 */
@Named
@ViewScoped
public class PhaseListController extends PaginationModel implements Serializable {

    private static final long serialVersionUID = 750598760775202929L;

    private static final String PROJECT_LIST_URL = "projects/projects.xhtml";

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectLevelProgressService projectLevelProgressService;

    @Getter
    private List<ProjectPhase> projectPhases;

    @Getter
    private List<ProjectTask> projectTasks;

    @Getter
    @Setter
    private ProjectLevelCategory projectLevelCategory;

    @Getter
    private Map<String, LevelCategoryChildStrategyBuilder> phaseMileStones;

    @Getter
    private Map<String, LevelCategoryChildStrategyBuilder> phaseTasks;

    @PostConstruct
    public void initPhases() {
        final String projectUUID = checkNotNull(pUUID.get(), "Project is not selected");
        final Project project = checkNotNull(projectService.getProject(pUUID.get()), "Project not found");
        setProjectLevelCategory(project.getProjectLevelCategory());
        projectPhases = new ArrayList<>();
        if (getProjectLevelCategory() == ProjectLevelCategory.TASK) {
            loadTasks(projectUUID);
        } else {
            loadPhases(projectUUID);
            prepareMilestoneList();
            prepareTasksList();
        }
    }

    private void loadTasks(@Nonnull final String projectUUID) {
        TaskContext.TaskContextBuilder builder = TaskContext
                .builder()
                .parentLevelUUID(projectUUID);

        final Future<Integer> countRequest = asynchronousService.execute(() -> {
            return projectTaskService.count(builder.build());
        });

        projectTasks = projectTaskService
                .getTasks(builder
                        .offset(getPage() * getLimit())
                        .limit(getLimit())
                        .build());
        try {
            this.setCount(countRequest.get());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private void loadPhases(@Nonnull final String projectUUID) {
        PhaseContext.PhaseContextBuilder builder = PhaseContext
                .builder()
                .projectUUID(projectUUID);
        final Future<Integer> countRequest = asynchronousService.execute(() -> {
            return projectPhaseService.count(builder.build());
        });
        projectPhases = projectPhaseService
                .getProjectPhases(builder
                        .offset(getPage() * getLimit())
                        .limit(getLimit())
                        .build());
        try {
            this.setCount(countRequest.get());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Profile
    private void prepareMilestoneList() {
        phaseMileStones = new HashMap<>();
        final List<String> phaseUUIDs = projectPhases
                .stream()
                .map(ProjectPhase::getUuid)
                .collect(Collectors.toList());

        final List<ProjectMilestone> mileStones = projectMilestoneService
                .findByPhaseUUIDs(phaseUUIDs);

        mileStones.stream().forEach((mileStone) -> {
            phaseMileStones.put(mileStone.getParentLevel().getUuid(),
                                LevelCategoryChildStrategyBuilder
                                .builder()
                                .rendered(false)
                                .count(getChildEntities(mileStone.getParentLevel().getUuid(), mileStones).size())
                                .childEntities(getChildEntities(mileStone.getParentLevel().getUuid(), mileStones))
                                .build());
        });
    }

    @Nonnull
    private List<? extends ProjectLevel> getChildEntities(@Nonnull final String uuid,
                                                          @Nonnull final List<? extends ProjectLevel> levels) {
        return levels.stream()
                .filter((level) -> level.getParentLevel().getUuid().equals(uuid))
                .collect((Collectors.toList()));
    }

    @Profile
    private void prepareTasksList() {
        phaseTasks = new HashMap<>();
        final List<String> uuids = new ArrayList<>();
        phaseMileStones.entrySet()
                .stream()
                .forEach((mile) -> {
                    mile.getValue()
                            .getChildEntities()
                            .stream()
                            .forEach((level) -> {
                                uuids.add(level.getUuid());
                            });
                });
        final List<ProjectTask> tasks = projectTaskService
                .findByMilestoneOrProjectUUIDs(uuids);
        prepareTasksForMilestone(tasks);

    }

    private void prepareTasksForMilestone(@Nonnull final List<ProjectTask> tasks) {
        tasks.stream()
                .forEach((task) -> {
                    phaseTasks.put(task.getParentLevel().getUuid(),
                                   LevelCategoryChildStrategyBuilder
                                   .builder()
                                   .rendered(false)
                                   .count(getChildEntities(task.getParentLevel().getUuid(), tasks).size())
                                   .childEntities(getChildEntities(task.getParentLevel().getUuid(), tasks))
                                   .build());
                });
    }

    @Override
    public void loadData() {
        initPhases();
    }

    @Nullable
    public ProjectLevelProgress getProjectLevelProgress(@Nonnull final String uuid) {
        return projectLevelProgressService
                .getProjectLevelProgresss(ImmutableList.of(uuid))
                .stream()
                .findFirst()
                .orElse(null);
    }

    public void showOrHideChilds(@Nonnull final String uuid) {
        if (phaseMileStones.containsKey(uuid)) {
            final LevelCategoryChildStrategyBuilder child = phaseMileStones.get(uuid);
            phaseMileStones
                    .replace(uuid, LevelCategoryChildStrategyBuilder
                             .builder()
                             .rendered(!child.isRendered())
                             .count(child.getCount())
                             .childEntities(child.getChildEntities())
                             .build());
        } else if (phaseTasks.containsKey(uuid)) {
            final LevelCategoryChildStrategyBuilder child = phaseTasks.get(uuid);
            phaseTasks
                    .replace(uuid, LevelCategoryChildStrategyBuilder
                             .builder()
                             .rendered(!child.isRendered())
                             .count(child.getCount())
                             .childEntities(child.getChildEntities())
                             .build());
        }
    }

    @Nonnull
    public String redirectProjectSummary() {
        final UrlContext.Builder context = UrlContext
                .builder()
                .path(PROJECT_LIST_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    public int countTaskList(@Nonnull final String uuid) {

        if (phaseMileStones.containsKey(uuid)) {
            return this.calculateTaskForPhase();
        } else if (phaseTasks.containsKey(uuid)) {
            return phaseTasks
                    .entrySet()
                    .stream()
                    .filter((phase) -> phase.getKey().equals(uuid))
                    .mapToInt((child) -> child.getValue().getCount())
                    .sum();
        }
        return 0;
    }

    private int calculateTaskForPhase() {
        int tasks = 0;
        final boolean hasMilestones = phaseMileStones
                .values()
                .stream()
                .flatMap((value) -> value.getChildEntities()
                        .stream())
                .anyMatch((milestone) -> phaseTasks.containsKey(milestone.getUuid()));
        if (hasMilestones) {
            tasks = phaseTasks
                    .entrySet()
                    .stream()
                    .mapToInt((child) -> child.getValue().getCount())
                    .sum();
        }
        return tasks;
    }
}
