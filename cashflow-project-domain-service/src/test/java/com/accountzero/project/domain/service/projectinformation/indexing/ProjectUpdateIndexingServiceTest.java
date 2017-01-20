package com.cashflow.project.domain.service.projectinformation.indexing;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.Project;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 *
 * 
 * @since Nov 14, 2016, 10:11:43 PM
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"serial", "FieldMayBeFinal"})
public class ProjectUpdateIndexingServiceTest {

    @InjectMocks
    private ProjectUpdateIndexingService projectUpdateIndexingService;

    @Test
    public void verifyProjectFound() {
        final Project project = new Project();
        final ProjectTest1 projectTest1 = new ProjectTest1(project, BigDecimal.ONE, Integer.SIZE);
        final NoProject noProject = new NoProject("name", project);
        final ProjectTest2 projectTest2 = new ProjectTest2(projectTest1, Integer.SIZE, noProject);
        final ProjectTest3 projectTest3 = new ProjectTest3(projectTest1, 99, project);
        final Project projectFound = projectUpdateIndexingService.getProject(projectTest3);
        assertThat(projectFound, is(project));

        final Project nextFoundProject = projectUpdateIndexingService.getProject(projectTest2);
        assertThat(nextFoundProject, is(project));
    }

    @Test
    public void verifyNoProject() {
        final NoProject noProject = new NoProject("name", new Object());
        final Project projectFound = projectUpdateIndexingService.getProject(noProject);
        assertThat(projectFound, is(nullValue()));
    }

    @Test
    public void verifyProject() {
        final Project project = new Project();
        final Project projectFound = projectUpdateIndexingService.getProject(project);
        assertThat(projectFound, is(project));
    }

    @AllArgsConstructor
    private static final class ProjectTest1 extends AbstractBusinessAccountDomain {

        private Project project;

        private BigDecimal value;

        private Integer offset;
    }

    @AllArgsConstructor
    private static final class ProjectTest2 extends AbstractBusinessAccountDomain {

        private ProjectTest1 projectTest1;

        private Integer offset;

        private NoProject noProject;
    }

    @AllArgsConstructor
    private static final class ProjectTest3 extends AbstractBusinessAccountDomain {

        private ProjectTest1 projectTest1;

        private Integer limit;

        private Project project;

    }

    @AllArgsConstructor
    private static final class NoProject extends AbstractBusinessAccountDomain {

        private String name;

        private Object project;
    }
}
