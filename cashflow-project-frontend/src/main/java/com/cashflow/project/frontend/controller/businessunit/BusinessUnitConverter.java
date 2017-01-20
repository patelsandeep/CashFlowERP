package com.cashflow.project.frontend.controller.businessunit;

import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.service.api.businessunit.BusinessUnitService;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

/**
 *
 * 
 * @since Oct 26, 2016, 5:20:32 PM
 */
@ApplicationScoped
@FacesConverter(forClass = BusinessUnit.class)
public class BusinessUnitConverter implements Converter {

    @Inject
    private BusinessUnitService businessUnitService;

    @Override
    public Object getAsObject(final FacesContext context, final UIComponent component, final String value) {
        if (value == null) {
            return null;
        }

        return businessUnitService.findBusinessUnit(value);
    }

    @Override
    public String getAsString(final FacesContext context, final UIComponent component, final Object value) {
        if (!(value instanceof BusinessUnit)) {
            return null;
        }

        return ((BusinessUnit) value).getUuid();
    }

}
