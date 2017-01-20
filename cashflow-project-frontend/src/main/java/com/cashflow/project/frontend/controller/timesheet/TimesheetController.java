package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.expenseinformation.TimesheetContext.TimesheetContextBuilder;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheetInfo;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.project.frontend.controller.businessunit.BusinessUnitEvent;
import com.cashflow.project.frontend.controller.businessunit.OnBusinessUnitSelected;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.TimesheetModel;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccountInformation;
import com.cashflow.useraccount.domain.businessunit.Branch;
import com.cashflow.useraccount.domain.businessunit.BusinessDivision;
import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.service.api.BusinessAccountInformationService;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.Getter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Jan 18, 2017, 3:23:52 PM
 */
@ModelViewScopedController
public class TimesheetController implements Serializable {

    private static final long serialVersionUID = -5040022600714922022L;

    @Inject
    private BusinessAccountInformationService businessAccountInformationService;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Inject
    private TimesheetHelper timesheetHelper;

    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    private BusinessUnitEvent businessUnitSelectionEvent;

    @Getter
    private TimesheetModel timesheetModel;

    @Getter
    private List<TimeSheet> allTimeSheetsForWeek = new ArrayList<>();

    @Getter
    private List<TimeSheet> allTimeSheetsForDay = new ArrayList<>();

    @PostConstruct
    public void initValues() {
        setBasicValues();
        updateTimesheets(TimesheetContext.builder());
    }

    public void onBusinessUnitSelectionEvent(
            @Observes(notifyObserver = Reception.IF_EXISTS) @OnBusinessUnitSelected final BusinessUnitEvent businessUnitSelectionEvent) {

        this.businessUnitSelectionEvent = businessUnitSelectionEvent;

        final TimesheetContextBuilder requestContext = TimesheetContext.builder();

        final Department department = businessUnitSelectionEvent.getSelectedDepartment();
        requestContext.departmentUUID(null != department ? department.getUuid() : null);

        final Branch branch = businessUnitSelectionEvent.getSelectedBranch();
        final BusinessDivision businessDivision = businessUnitSelectionEvent.getSelectedBusinessDivision();
        if (branch != null) {
            requestContext.businessUnitUUID(branch.getUuid());
        } else if (businessDivision != null) {
            requestContext.businessUnitUUID(businessDivision.getUuid());
        }

        updateTimesheets(requestContext);
    }

    @Nonnull
    public String isToday(@Nonnull final Date toCheckDate) {
        final Calendar toCheck = Calendar.getInstance(TimeZone.getTimeZone(timesheetModel.getTimeZone()));
        toCheck.setTime(toCheckDate);

        final Calendar now = Calendar.getInstance(TimeZone.getTimeZone(timesheetModel.getTimeZone()));

        if (now.get(Calendar.ERA) == toCheck.get(Calendar.ERA)
                && now.get(Calendar.YEAR) == toCheck.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == toCheck.get(Calendar.DAY_OF_YEAR)) {
            return "alt";
        } else {
            return "";
        }
    }

    private void setBasicValues() {
        timesheetModel = new TimesheetModel();

        timesheetModel.setTimesheetViewMode(TimesheetViewMode.WEEK);

        final BusinessAccountInformation businessAccountInfo = businessAccountInformationService
                .findBusinessAccountInformation();
        timesheetModel.setTimeZone(null != businessAccountInfo ? businessAccountInfo.getTimezone() : "GMT");

        final TimeZone tz = TimeZone.getTimeZone(timesheetModel.getTimeZone());

        final DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(tz);

        final DateTime toDay = new DateTime(dateTimeZone);

        final DateTime monday = toDay.withDayOfWeek(DateTimeConstants.MONDAY);
        timesheetModel.setCurrentDate(Calendar.getInstance(tz));
        timesheetModel.setMonday(monday.toDate());
        timesheetModel.setTuesday(monday.plusDays(1).toDate());
        timesheetModel.setWednesday(monday.plusDays(2).toDate());
        timesheetModel.setThursday(monday.plusDays(3).toDate());
        timesheetModel.setFriday(monday.plusDays(4).toDate());
        timesheetModel.setSaturday(monday.plusDays(5).toDate());
        timesheetModel.setSunday(monday.plusDays(6).toDate());
        timesheetModel.setTotalHoursPerWeek("0.00");

    }

    @Nonnull
    private String getBusinessUnitUUID() {
        final AuthorizedUser authorizedUser = checkNotNull(this.authorizedUser.get());
        final BusinessUnit<?> businessUnit = authorizedUser.getBusinessUnit();
        if (isInCurrentCompanyAccount(businessUnit)) {
            return businessUnit.getUuid();
        }

        return companyAccount.get().getUuid();
    }

    @Nullable
    private String getDepartmentUUID() {
        final EmployeeInformation emp = employeeInformationService
                .getEmployeeInformation(authorizedUser.get().getUuid());
        if (null != emp.getDepartment()) {
            return emp.getDepartment().getUuid();
        }
        return null;
    }

    private boolean isInCurrentCompanyAccount(@Nonnull final BusinessUnit<?> businessUnit) {
        if (Objects.equals(companyAccount.get().getUuid(),
                           businessUnit.getUuid())) {
            return true;
        }

        final BusinessUnit<?> parentUnit = businessUnit.getParentUnit();
        if (parentUnit == null) {
            return false;
        }

        return isInCurrentCompanyAccount(parentUnit);
    }

    private void updateTimesheets(@Nonnull final TimesheetContextBuilder timesheetContext) {
        final TimeZone tz = TimeZone.getTimeZone(timesheetModel.getTimeZone());

        if (businessUnitSelectionEvent == null) {
            timesheetContext.businessUnitUUID(getBusinessUnitUUID());
            timesheetContext.departmentUUID(getDepartmentUUID());
        }
        if (timesheetModel.getTimesheetViewMode() == TimesheetViewMode.WEEK) {
            final Calendar monday_ = Calendar.getInstance(tz);
            monday_.setTime(timesheetModel.getMonday());
            monday_.set(Calendar.HOUR_OF_DAY, 0);
            monday_.set(Calendar.MINUTE, 0);
            monday_.set(Calendar.SECOND, 0);

            final Calendar sunday_ = Calendar.getInstance(tz);
            sunday_.setTime(timesheetModel.getSunday());
            sunday_.set(Calendar.HOUR_OF_DAY, 23);
            sunday_.set(Calendar.MINUTE, 59);
            sunday_.set(Calendar.SECOND, 59);

            allTimeSheetsForWeek = timesheetHelper.getBetweenTimesheets(timesheetContext, monday_, sunday_);
        } else if (timesheetModel.getTimesheetViewMode() == TimesheetViewMode.DAY) {
            final Calendar currentDate = timesheetModel.getCurrentDate();

            allTimeSheetsForDay = timesheetHelper.getTimesheetsByDate(timesheetContext, currentDate);
        }
    }

    @Nonnull
    public WorkTime getHours(@Nonnull final Date date) {

        final List<TimeSheetInfo> infos = allTimeSheetsForWeek.stream()
                .map((time) -> new ArrayList<>(time.getTimeSheetInfos()))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        final Map<Date, List<TimeSheetInfo>> infosMap = infos
                .stream()
                .collect(Collectors.groupingBy((info) -> info.getTimeSheetDate().getTime()));

        final List<TimeSheetInfo> infoForDate = infosMap.get(date);

        final WorkTime wt = new WorkTime();

        final WorkTime totalforDate = infoForDate
                .stream()
                .map((info) -> info.getOverTime().add(info.getPtoTime()).add(info.getRegularTime()))
                .reduce((w1, w2) -> w1.add(w2)).orElse(wt);

        return totalforDate;

    }

}
