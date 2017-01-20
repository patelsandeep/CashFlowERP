package com.cashflow.project.domain.facade;

import com.cashflow.entitydomains.AbstractDomain;
import com.cashflow.entitydomains.facade.AbstractFacade;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 12, 2016, 1:38:25 PM
 */
@Dependent
public abstract class ProjectAbstractFacade<T extends AbstractDomain> extends AbstractFacade<T> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    protected EntityManager entityManager;

    public ProjectAbstractFacade(@Nonnull final Class<T> entityClass) {
        super(checkNotNull(entityClass, "The entityClass must not be null"));
    }

}
