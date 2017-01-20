package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.domain.supplier.SupplierSearchContext;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheetInfo;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.PendingTimeSheetModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.timesheet.PendingTimeSheetTranslationService;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.domain.businessuser.employee.EmployeeInformationSearchContext;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.anosym.profiler.Profile;
import com.anosym.urlbuilder.QueryParameter;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MEMBER_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MENU;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since 21 Dec, 2016, 11:44:45 AM
 */
@ModelViewScopedController
public class PendingTimeSheetController implements Serializable {

    private static final long serialVersionUID = 1207742156797600231L;

    private static final String PROJECT_APPROVE_TIMESHEET_URL = "/timesheet/pending-timesheet-list.xhtml";

    private static final String PROJECT_TIMESHEET_URL = "/timesheet/timesheets.xhtml";

    private static final String PROJECT_APPROVE_TIMESHEET_DETAIL_URL = "/timesheet/pending-timesheet-detail.xhtml";

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private SupplierService supplierService;

    @Inject
    private TimeSheetService timeSheetService;

    @Inject
    private ProjectTimeSheetProcessor projectTimeSheetProcessor;

    @Inject
    private PendingTimeSheetTranslationService pendingTimeSheetTranslationService;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    @Nonnull
    private List<Calendar> dates = new ArrayList<>();

    @Getter
    @Setter
    @Nullable
    private List<Supplier> suppliers = new ArrayList<>();

    @Getter
    @Setter
    @Nullable
    private String sortBy = "People";

    @Getter
    @Setter
    @Nullable
    private String projectUUID;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Getter
    @Setter
    @Nullable
    private Map<String, EmployeeInformation> users;

    @Getter
    @Setter
    @Nullable
    private Map<String, PendingTimeSheetModel> pendingTimeSheetInfo;

    @Getter
    @Setter
    @Nullable
    private Map<String, Boolean> checked = new HashMap<>();

    private Map<String, ProjectLevel<?>> levels;

    @Nullable
    @Profile
    public Map<String, PendingTimeSheetModel> getPendingTimeSheet() {

        final TimesheetContext timesheetContext = TimesheetContext
                .builder()
                .status(ExpenseStatus.SUBMITTED)
                .build();
        final List<TimeSheet> timesheets = timeSheetService.getTimesheets(timesheetContext);
        Map<String, List<TimeSheet>> timeSheetsMap = new HashMap<>();
        if (sortBy.equals("People")) {
            timeSheetsMap = timesheets
                    .stream()
                    .collect(Collectors.groupingBy((timesheet) -> getTimesheetMemberUUID(timesheet)));
        } else {
            timeSheetsMap = timesheets
                    .stream()
                    .collect(Collectors.groupingBy((timesheet) -> getTimesheetProjectUUID(timesheet)));
        }
        final List<PendingTimeSheetModel> pendingTimeSheetList = this.getTimesheetInfo(timeSheetsMap);

        if (sortBy.equals("People")) {
            pendingTimeSheetInfo = pendingTimeSheetList
                    .stream()
                    .collect(Collectors.toMap((model) -> model.getMemberUUID(),
                                              (model) -> prepareValue(model, model.getMemberUUID())));

        } else {
            pendingTimeSheetInfo = pendingTimeSheetList
                    .stream()
                    .collect(Collectors.toMap((model) -> model.getProjectUUID(),
                                              (model) -> prepareValue(model, model.getProjectUUID())));
        }

        checked = pendingTimeSheetInfo
                .entrySet()
                .stream()
                .collect(Collectors.toMap((model) -> model.getKey(),
                                          (model) -> model.getValue().isCheck()));

        return pendingTimeSheetInfo;
    }

    @Nonnull
    public PendingTimeSheetModel prepareAllTimeInfo(@Nonnull final List<TimeSheet> timeSheets) {

        final PendingTimeSheetModel info = new PendingTimeSheetModel();
        loadMembers(timeSheets);
        loadSuppliers(timeSheets);
        WorkTime totalBillableRegularHours = new WorkTime();
        WorkTime totalBillablePtoHours = new WorkTime();
        WorkTime totalBillableOverTimeHours = new WorkTime();
        WorkTime totalNonBillableRegularHours = new WorkTime();
        WorkTime totalNonBillablePtoHours = new WorkTime();
        WorkTime totalNonBillableOverTimeHours = new WorkTime();

        WorkTime totalBillableHours = new WorkTime();
        WorkTime totalNonBillableHours = new WorkTime();
        for (TimeSheet report : timeSheets) {

            levels = new HashMap<>();
            setProjectLevels(report.getProjectLevel());
            levels
                    .entrySet()
                    .forEach(level -> {
                        if (level.getValue() instanceof Project) {
                            info.setProjectUUID(((Project) level.getValue()).getUuid());
                            projectUUID = ((Project) level.getValue()).getUuid();
                            info.setProject(((Project) level.getValue()).getName());
                        }
                    });

            info.setTimeSheetUUID(report.getUuid());
            if (null != report.getTeamMember()) {
                final EmployeeInformation employeeInfo = users.get(report.getTeamMember().getEmployeeUUID());

                info.setMember(employeeInfo.getEmployee().getName());
                info.setMemberUUID(report.getTeamMember().getUuid());
            } else {
                for (Supplier supplier : suppliers) {
                    if (supplier.getUuid().equals(report.getSubContractor().getSubContractorUUID())) {
                        final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
                        contactPersons.add(supplier.getContactPerson());
                        contactPersons.stream()
                                .filter((cp) -> (cp.getUuid().equals(report.getSubContractor().getMemberName())))
                                .forEachOrdered((cp) -> {
                                    info.setMember(cp.getName());
                                });
                    }
                }
                info.setMemberUUID(report.getSubContractor().getUuid());
            }

            for (TimeSheetInfo sheetInfo : report.getTimeSheetInfos()) {
                dates.add(sheetInfo.getTimeSheetDate());
            }

            info.setTimesheetDate(Collections.min(dates));
            info.setToTimesheetDate(Collections.max(dates));

            final WorkTime regularhours = report.getTimeSheetInfos()
                    .stream()
                    .filter((time) -> time.getBillableType() == BillableType.YES)
                    .map(TimeSheetInfo::getRegularTime)
                    .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
            totalBillableRegularHours = totalBillableRegularHours.add(regularhours);

            final WorkTime ptoHours = report.getTimeSheetInfos()
                    .stream()
                    .filter((time) -> time.getBillableType() == BillableType.YES)
                    .map(TimeSheetInfo::getPtoTime)
                    .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

            totalBillablePtoHours = totalBillablePtoHours.add(ptoHours);

            final WorkTime overTimeHours = report.getTimeSheetInfos()
                    .stream()
                    .filter((time) -> time.getBillableType() == BillableType.YES)
                    .map(TimeSheetInfo::getOverTime)
                    .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
            totalBillableOverTimeHours = totalBillableOverTimeHours.add(overTimeHours);
            totalBillableHours = totalBillableOverTimeHours.add(totalBillablePtoHours).add(totalBillableRegularHours);
            info.setBillableHours(totalBillableHours);

            final WorkTime nonBillRegularhours = report.getTimeSheetInfos()
                    .stream()
                    .filter((time) -> time.getBillableType() == BillableType.NO)
                    .map(TimeSheetInfo::getRegularTime)
                    .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
            totalNonBillableRegularHours = totalNonBillableRegularHours.add(nonBillRegularhours);

            final WorkTime nonBillPtoHours = report.getTimeSheetInfos()
                    .stream()
                    .filter((time) -> time.getBillableType() == BillableType.NO)
                    .map(TimeSheetInfo::getPtoTime)
                    .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

            totalNonBillablePtoHours = totalNonBillablePtoHours.add(nonBillPtoHours);

            final WorkTime nonBillOverTimeHours = report.getTimeSheetInfos()
                    .stream()
                    .filter((time) -> time.getBillableType() == BillableType.NO)
                    .map(TimeSheetInfo::getOverTime)
                    .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
            totalNonBillableOverTimeHours = totalNonBillableOverTimeHours.add(nonBillOverTimeHours);
            totalNonBillableHours = totalNonBillableOverTimeHours.add(totalNonBillablePtoHours).add(
                    totalNonBillableRegularHours);
            info.setNonBillableHours(totalNonBillableHours);

            info.setTotalHours(totalBillableHours.add(totalNonBillableHours));
        }
        return info;
    }

    @Nonnull
    public String redirectApproveTimeSheet() {
        final List<QueryParameter<?>> projectUUIDParam = ImmutableList
                .<QueryParameter<?>>builder()
                .add(buildQueryParameter(SELECTED_MENU, MenuOption.TIMESHEETS.name()))
                .build();

        final UrlContext.Builder context = UrlContext
                .builder()
                .additionalParameters(projectUUIDParam)
                .path(PROJECT_APPROVE_TIMESHEET_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    public void setPeopleSorting() {
        setSortBy("People");
    }

    public void setProjectSorting() {
        setSortBy("Project");
    }

    @Profile
    public void approvePendingTimeSheet() {
        if (null == checked && checked.containsValue(false)) {
            successMessageModel = new SuccessMessageModel();
            successMessageModel.setSuccessMessage(pendingTimeSheetTranslationService.getSelectTimeSheetLabel());
            return;
        }
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        final List<TimeSheet> timesheets;
        if (sortBy.equals("People")) {
            final List<String> peopleUUID = pendingTimeSheetInfo.values()
                    .stream()
                    .filter((member) -> checked.get(member.getUuid()))
                    .map((teammember) -> teammember.getUuid())
                    .collect(Collectors.toList());

            final TimesheetContext timesheetContext = TimesheetContext
                    .builder()
                    .peopleUUIDs(peopleUUID)
                    .build();
            timesheets = timeSheetService.getTimesheets(timesheetContext);
        } else {
            final List<String> timesheetUUIDs = pendingTimeSheetInfo.values()
                    .stream()
                    .filter((member) -> checked.get(member.getUuid()))
                    .map((teammember) -> teammember.getTimeSheetUUID())
                    .collect(Collectors.toList());

            final TimesheetContext timesheetContext = TimesheetContext
                    .builder()
                    .timesheetUUIDs(timesheetUUIDs)
                    .build();
            timesheets = timeSheetService.getTimesheets(timesheetContext);
        }
        projectTimeSheetProcessor.approveTimeSheet(timesheets, action, projectUUID);
        successMessageModel = new SuccessMessageModel();
        if (action.equals("approve")) {
            successMessageModel.setSuccessMessage(pendingTimeSheetTranslationService.getApproveSuccessMessage());
        } else {
            successMessageModel.setSuccessMessage(pendingTimeSheetTranslationService.getRejectSuccessMessage());
        }
    }

    @Nonnull
    public String buildTimesheetDetailUrl(@Nonnull final String uuid) {
        final List<QueryParameter<?>> pendingTimeSheetUUIDParams;
        if (sortBy.equals("People")) {
            pendingTimeSheetUUIDParams = ImmutableList
                    .<QueryParameter<?>>builder()
                    .add(buildQueryParameter(SELECTED_MENU, MenuOption.TIMESHEETS.name()))
                    .add(buildQueryParameter(SELECTED_MEMBER_UUID, uuid))
                    .build();

        } else {
            pendingTimeSheetUUIDParams = ImmutableList
                    .<QueryParameter<?>>builder()
                    .add(buildQueryParameter(SELECTED_MENU, MenuOption.TIMESHEETS.name()))
                    .add(buildQueryParameter(SELECTED_PROJECT_UUID, uuid))
                    .build();

        }

        final UrlContext urlContext = UrlContext
                .builder()
                .additionalParameters(pendingTimeSheetUUIDParams)
                .forceFacesRedirect(true)
                .path(PROJECT_APPROVE_TIMESHEET_DETAIL_URL)
                .build();
        return staticLinkUrlBuilder.buildURL(urlContext);
    }

    @Nonnull
    public String redirectTimeSheet() {
        final QueryParameter parameter = QueryParameter
                .<String>builder()
                .parameter(SELECTED_MENU)
                .parameterValue(MenuOption.TIMESHEETS.name())
                .build();

        final UrlContext.Builder context = UrlContext
                .builder()
                .additionalParameter(parameter)
                .path(PROJECT_TIMESHEET_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    @Nonnull
    private PendingTimeSheetModel prepareValue(@Nonnull final PendingTimeSheetModel pendingTimeSheetModel,
                                               @Nonnull final String uuid) {
        pendingTimeSheetModel.setUuid(uuid);
        pendingTimeSheetModel.setCheck(false);
        return pendingTimeSheetModel;
    }

    @Nonnull
    private String getTimesheetMemberUUID(@Nonnull final TimeSheet timeSheet) {
        if (timeSheet.getTeamMember() != null) {
            return timeSheet.getTeamMember().getUuid();
        } else {
            return timeSheet.getSubContractor().getUuid();
        }
    }

    @Nonnull
    private String getTimesheetProjectUUID(@Nonnull final TimeSheet timeSheet) {
        levels = new HashMap<>();
        setProjectLevels(timeSheet.getProjectLevel());

        for (ProjectLevel projectLevel : levels.values()) {
            if (projectLevel instanceof Project) {
                return ((Project) projectLevel).getUuid();
            }
        }
        return null;

    }

    @Nonnull
    private List<PendingTimeSheetModel> getTimesheetInfo(@Nonnull final Map<String, List<TimeSheet>> timesMap) {
        return timesMap.entrySet()
                .stream()
                .map((report) -> this.prepareAllTimeInfo(report.getValue()))
                .collect(Collectors.toList());
    }

    private void loadMembers(@Nonnull final List<TimeSheet> timesheets) {
        final List<String> employeeUUIDs = timesheets
                .stream()
                .filter((sheet) -> null != sheet.getTeamMember())
                .map((teammember) -> teammember.getTeamMember().getEmployeeUUID())
                .collect(Collectors.toList());

        final EmployeeInformationSearchContext searchContext = EmployeeInformationSearchContext
                .builder()
                .employeeNumbersOrUuids(employeeUUIDs)
                .build();
        final List<EmployeeInformation> employees = employeeInformationService.getEmployeeInformations(searchContext)
                .getEmployeeInformations();
        users = employees
                .stream()
                .collect(Collectors.toMap((ei) -> ei.getEmployee().getUuid(), Function.identity()));
    }

    private void loadSuppliers(@Nonnull final List<TimeSheet> timesheets) {
        final List<String> supplierUUIDs = timesheets
                .stream()
                .filter((sheet) -> null != sheet.getSubContractor())
                .map((sc) -> sc.getSubContractor().getSubContractorUUID())
                .collect(Collectors.toList());
        final SupplierSearchContext searchContext = SupplierSearchContext.builder()
                .supplierUUIDs(supplierUUIDs)
                .offset(0)
                .limit(100)
                .build();

        suppliers = supplierService.findSuppliers(searchContext);
    }

    @Nonnull
    private QueryParameter<String> buildQueryParameter(@Nonnull final String parameter, @Nonnull String parameterValue) {
        return projectTimeSheetProcessor.buildQueryParameter(parameter, parameterValue);
    }

    private void setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
    }
}
