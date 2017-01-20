package com.cashflow.project.frontend.timesheet;

import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.project.domain.project.timesheet.WorkTimeJPAConverter;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 *
 * 
 * @since Dec 5, 2016, 1:49:16 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkTimeJPAConverterTest {

    @InjectMocks
    private WorkTimeJPAConverter timeJPAConverter;

    @Test
    public void testConvertToDB() {
        final WorkTime wt = new WorkTime();
        wt.setTotalHours(0);
        wt.setTotalMinutes(50);
        final BigDecimal bd = timeJPAConverter.convertToDatabaseColumn(wt);
        final BigDecimal exPecedBd = new BigDecimal(1800);
        assertEquals(bd, exPecedBd);
    }

    @Test
    public void testConvertToEntity() {
        WorkTime wt = timeJPAConverter.convertToEntityAttribute(new BigDecimal(5400));
        assertEquals(wt.getTotalHours(), 1);
        assertEquals(wt.getTotalMinutes(), 50);
    }

    @Test
    public void testConvertToDB2() {
        final WorkTime wt = new WorkTime();
        wt.setTotalHours(10);
        wt.setTotalMinutes(50);
        final BigDecimal bd = timeJPAConverter.convertToDatabaseColumn(wt);
        final BigDecimal exPecedBd = new BigDecimal(37800);
        assertEquals(bd, exPecedBd);
    }

    @Test
    public void testConvertToEntity2() {
        WorkTime wt = timeJPAConverter.convertToEntityAttribute(new BigDecimal(37800));
        assertEquals(wt.getTotalHours(), 10);
        assertEquals(wt.getTotalMinutes(), 50);
    }

}
