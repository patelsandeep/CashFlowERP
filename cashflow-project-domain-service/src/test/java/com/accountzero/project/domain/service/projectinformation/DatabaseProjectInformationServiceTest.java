package com.cashflow.project.domain.service.projectinformation;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.project.domain.facade.budget.BudgetFacade;
import com.cashflow.project.domain.facade.contextual.ProjectContext;
import com.cashflow.project.domain.facade.contextual.ProjectContextFacade;
import com.cashflow.project.domain.facade.invoice.InvoiceFacade;
import com.cashflow.project.domain.facade.level.ProjectLevelProgressFacade;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.google.common.collect.ImmutableList;
import java.util.Calendar;
import java.util.logging.Logger;
import javax.enterprise.inject.Instance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 *
 * 
 * @since Nov 14, 2016, 7:42:31 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class DatabaseProjectInformationServiceTest {

    @Mock
    private ProjectContextFacade projectContextFacade;

    @Mock
    private BudgetFacade budgetFacade;

    @Mock
    private ProjectLevelProgressFacade projectLevelProgressFacade;

    @Mock
    private InvoiceFacade invoiceFacade;

    @Mock
    private AsynchronousService asynchronousService;

    @Mock
    private EmployeeInformationService employeeInformationService;

    @Mock
    private Logger logger;

    @Mock
    private Instance<BusinessAccount> businessAccount;

    @InjectMocks
    private DatabaseProjectInformationService databaseProjectInformationService;

    @Test
    public void verifyFilterNotProjectDomain() {
        final Filter filter = Filter
                .builder()
                .attribute("createdFromDate")
                .name("Created From")
                .build();
        final Object createdFrom = Calendar.getInstance();
        final ProjectContext projectContext = new ProjectContext();
        databaseProjectInformationService.setFilter(filter, createdFrom, projectContext);

        assertThat(projectContext.getCreatedFromDate(), is(nullValue()));
    }

    @Test
    public void verifyFilterIsProjectDomain() {
        final Filter filter = Filter
                .builder()
                .attribute("createdFromDate")
                .name("Created From")
                .filterDomains(ImmutableList.of(FilterDomain.PROJECT))
                .build();
        final Object createdFrom = Calendar.getInstance();
        final ProjectContext projectContext = new ProjectContext();
        databaseProjectInformationService.setFilter(filter, createdFrom, projectContext);

        assertThat(projectContext.getCreatedFromDate(), is(equalTo(createdFrom)));
    }

}
