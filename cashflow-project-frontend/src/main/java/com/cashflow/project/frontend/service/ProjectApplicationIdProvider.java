package com.cashflow.project.frontend.service;

import com.cashflow.access.authorization.ApplicationIdProvider;
import com.cashflow.product.urlbuilder.domain.Product;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * 
 * @since Dec 23, 2016, 1:24:34 PM
 */
@ApplicationScoped
public class ProjectApplicationIdProvider implements ApplicationIdProvider {

    @Nonnull
    @Override
    public Product getApplicationId() {
        return Product.PROJECTS;
    }

}
