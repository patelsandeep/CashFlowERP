package com.cashflow.project.domain.facade.task;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since Dec 9, 2016, 3:56:21 PM
 */
@Stateless
public class TaskContextFacade extends ContextFacade<TaskContext, ProjectTask> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    private EntityManager entityManager;

    public TaskContextFacade() {
        super(ProjectTask.class, new TaskContextQueryBuilderImpl());
    }

    @SuppressWarnings("AnnotationAsSuperInterface") //CDI
    private static final class TaskContextQueryBuilderImpl extends AnnotationLiteral<TaskContextQueryBuilder>
            implements TaskContextQueryBuilder {

        private static final long serialVersionUID = 8084938212826752139L;

    }
}
