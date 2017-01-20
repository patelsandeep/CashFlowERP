package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.project.frontend.controller.model.PendingTimeSheetModel;
import com.cashflow.project.frontend.controller.model.TimeSheetInfoModel;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;

/**
 *
 * 
 * @since 31 Dec, 2016, 10:25:46 AM
 */
@Dependent
public class PendingTimeSheetCalculationProcessor implements Serializable {

    private static final long serialVersionUID = -5355086830214871937L;

    @Nonnull
    public WorkTime calculateBillableHours(@Nonnull final Calendar timeSheetDate,
                                           @Nonnull final Map<Calendar, List<TimeSheetInfoModel>> pendingTimeSheetInfoModel,
                                           @Nonnull final PendingTimeSheetModel pendingTimeSheetModel) {
        WorkTime totalBillableHours = new WorkTime();
        WorkTime totalBillableRegularHours = new WorkTime();
        WorkTime totalBillablePtoHours = new WorkTime();
        WorkTime totalBillableOverTimeHours = new WorkTime();
        final List<TimeSheetInfoModel> timeSheetInfos = pendingTimeSheetInfoModel.get(timeSheetDate);

        final WorkTime regularhours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES)
                .map(TimeSheetInfoModel::getRegularTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalBillableRegularHours = totalBillableRegularHours.add(regularhours);

        final WorkTime ptoHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES)
                .map(TimeSheetInfoModel::getPtoTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

        totalBillablePtoHours = totalBillablePtoHours.add(ptoHours);

        final WorkTime overTimeHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES)
                .map(TimeSheetInfoModel::getOverTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalBillableOverTimeHours = totalBillableOverTimeHours.add(overTimeHours);
        totalBillableHours = totalBillableOverTimeHours.add(totalBillablePtoHours).add(totalBillableRegularHours);
        pendingTimeSheetModel.setBillableHours(pendingTimeSheetModel.getBillableHours().add(totalBillableHours));
        return totalBillableHours;
    }

    @Nonnull
    public WorkTime calculateNonBillableHours(@Nonnull final Calendar timeSheetDate,
                                              @Nonnull final Map<Calendar, List<TimeSheetInfoModel>> pendingTimeSheetInfoModel,
                                              @Nonnull final PendingTimeSheetModel pendingTimeSheetModel) {
        WorkTime totalNonBillableHours = new WorkTime();
        WorkTime totalNonBillableRegularHours = new WorkTime();
        WorkTime totalNonBillablePtoHours = new WorkTime();
        WorkTime totalNonBillableOverTimeHours = new WorkTime();
        final List<TimeSheetInfoModel> timeSheetInfos = pendingTimeSheetInfoModel.get(timeSheetDate);

        final WorkTime regularhours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO)
                .map(TimeSheetInfoModel::getRegularTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalNonBillableRegularHours = totalNonBillableRegularHours.add(regularhours);

        final WorkTime ptoHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO)
                .map(TimeSheetInfoModel::getPtoTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

        totalNonBillablePtoHours = totalNonBillablePtoHours.add(ptoHours);

        final WorkTime overTimeHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO)
                .map(TimeSheetInfoModel::getOverTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalNonBillableOverTimeHours = totalNonBillableOverTimeHours.add(overTimeHours);
        totalNonBillableHours = totalNonBillableOverTimeHours.add(totalNonBillablePtoHours).add(
                totalNonBillableRegularHours);

        pendingTimeSheetModel
                .setNonBillableHours(pendingTimeSheetModel.getNonBillableHours().add(totalNonBillableHours));
        return totalNonBillableHours;
    }

    @Nonnull
    public WorkTime calculateProjectInfoBillableHours(@Nonnull final TimeSheetInfoModel infoModel,
                                                      @Nonnull final Map<Calendar, List<TimeSheetInfoModel>> pendingTimeSheetInfoModel) {
        WorkTime totalBillableHours = new WorkTime();
        WorkTime totalBillableRegularHours = new WorkTime();
        WorkTime totalBillablePtoHours = new WorkTime();
        WorkTime totalBillableOverTimeHours = new WorkTime();
        final List<TimeSheetInfoModel> timeSheetInfos = pendingTimeSheetInfoModel.get(infoModel.getTimeSheetInfoDate());

        final WorkTime regularhours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES && time.getProjectLevel().equals(infoModel
                .getProjectLevel()))
                .map(TimeSheetInfoModel::getRegularTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalBillableRegularHours = totalBillableRegularHours.add(regularhours);

        final WorkTime ptoHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES && time.getProjectLevel().equals(infoModel
                .getProjectLevel()))
                .map(TimeSheetInfoModel::getPtoTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

        totalBillablePtoHours = totalBillablePtoHours.add(ptoHours);

        final WorkTime overTimeHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES && time.getProjectLevel().equals(infoModel
                .getProjectLevel()))
                .map(TimeSheetInfoModel::getOverTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalBillableOverTimeHours = totalBillableOverTimeHours.add(overTimeHours);
        totalBillableHours = totalBillableOverTimeHours.add(totalBillablePtoHours).add(totalBillableRegularHours);

        return totalBillableHours;
    }

    @Nonnull
    public WorkTime calculateProjectInfoNonBillableHours(@Nonnull final TimeSheetInfoModel infoModel,
                                                         @Nonnull final Map<Calendar, List<TimeSheetInfoModel>> pendingTimeSheetInfoModel) {
        WorkTime totalNonBillableHours = new WorkTime();
        WorkTime totalNonBillableRegularHours = new WorkTime();
        WorkTime totalNonBillablePtoHours = new WorkTime();
        WorkTime totalNonBillableOverTimeHours = new WorkTime();
        final List<TimeSheetInfoModel> timeSheetInfos = pendingTimeSheetInfoModel.get(infoModel.getTimeSheetInfoDate());
        final WorkTime regularhours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO && time.getProjectLevel().equals(infoModel
                .getProjectLevel()))
                .map(TimeSheetInfoModel::getRegularTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalNonBillableRegularHours = totalNonBillableRegularHours.add(regularhours);

        final WorkTime ptoHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO && time.getProjectLevel().equals(infoModel
                .getProjectLevel()))
                .map(TimeSheetInfoModel::getPtoTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

        totalNonBillablePtoHours = totalNonBillablePtoHours.add(ptoHours);

        final WorkTime overTimeHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO && time.getProjectLevel().equals(infoModel
                .getProjectLevel()))
                .map(TimeSheetInfoModel::getOverTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalNonBillableOverTimeHours = totalNonBillableOverTimeHours.add(overTimeHours);
        totalNonBillableHours = totalNonBillableOverTimeHours.add(totalNonBillablePtoHours).add(
                totalNonBillableRegularHours);

        return totalNonBillableHours;
    }

    @Nonnull
    public WorkTime calculateMemberInfoBillableHours(@Nonnull final TimeSheetInfoModel infoModel,
                                                     @Nonnull final Map<Calendar, List<TimeSheetInfoModel>> pendingTimeSheetInfoModel) {
        WorkTime totalBillableHours = new WorkTime();
        WorkTime totalBillableRegularHours = new WorkTime();
        WorkTime totalBillablePtoHours = new WorkTime();
        WorkTime totalBillableOverTimeHours = new WorkTime();
        final List<TimeSheetInfoModel> timeSheetInfos = pendingTimeSheetInfoModel.get(infoModel.getTimeSheetInfoDate());

        final WorkTime regularhours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES && time.getMemberId().equals(infoModel
                .getMemberId()))
                .map(TimeSheetInfoModel::getRegularTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalBillableRegularHours = totalBillableRegularHours.add(regularhours);

        final WorkTime ptoHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES && time.getMemberId().equals(infoModel
                .getMemberId()))
                .map(TimeSheetInfoModel::getPtoTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

        totalBillablePtoHours = totalBillablePtoHours.add(ptoHours);

        final WorkTime overTimeHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES && time.getMemberId().equals(infoModel
                .getMemberId()))
                .map(TimeSheetInfoModel::getOverTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalBillableOverTimeHours = totalBillableOverTimeHours.add(overTimeHours);
        totalBillableHours = totalBillableOverTimeHours.add(totalBillablePtoHours).add(totalBillableRegularHours);

        return totalBillableHours;
    }

    @Nonnull
    public WorkTime calculateMemberInfoNonBillableHours(@Nonnull final TimeSheetInfoModel infoModel,
                                                        @Nonnull final Map<Calendar, List<TimeSheetInfoModel>> pendingTimeSheetInfoModel) {
        WorkTime totalNonBillableHours = new WorkTime();
        WorkTime totalNonBillableRegularHours = new WorkTime();
        WorkTime totalNonBillablePtoHours = new WorkTime();
        WorkTime totalNonBillableOverTimeHours = new WorkTime();
        final List<TimeSheetInfoModel> timeSheetInfos = pendingTimeSheetInfoModel.get(infoModel.getTimeSheetInfoDate());
        final WorkTime regularhours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO && time.getMemberId().equals(infoModel
                .getMemberId()))
                .map(TimeSheetInfoModel::getRegularTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalNonBillableRegularHours = totalNonBillableRegularHours.add(regularhours);

        final WorkTime ptoHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO && time.getMemberId().equals(infoModel
                .getMemberId()))
                .map(TimeSheetInfoModel::getPtoTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

        totalNonBillablePtoHours = totalNonBillablePtoHours.add(ptoHours);

        final WorkTime overTimeHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO && time.getMemberId().equals(infoModel
                .getMemberId()))
                .map(TimeSheetInfoModel::getOverTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalNonBillableOverTimeHours = totalNonBillableOverTimeHours.add(overTimeHours);
        totalNonBillableHours = totalNonBillableOverTimeHours.add(totalNonBillablePtoHours).add(
                totalNonBillableRegularHours);

        return totalNonBillableHours;
    }

}
