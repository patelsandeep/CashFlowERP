package com.cashflow.project.domain.service.projectinformation.indexing;

import com.cashflow.indexing.service.provider.ObjectMapperProvider;
import com.cashflow.module.authentication.serialization.provider.ObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * 
 * @since Jul 19, 2016, 5:41:04 AM
 */
@ApplicationScoped
public class IndexingObjectMapperProvider implements ObjectMapperProvider {

    @Nonnull
    @Override
    public ObjectMapper provide() {
        return ObjectMapperFactory.getObjectMapper();
    }

}
