package com.cashflow.project.frontend.controller.project;

import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.service.api.EmployeeService;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

/**
 *
 * 
 */
@ApplicationScoped
@FacesConverter(forClass = Employee.class)
public class EmployeeConverter implements Converter {

    @Inject
    private EmployeeService employeeService;

    @Override
    public Object getAsObject(final FacesContext context,
            final UIComponent component,
            @Nullable final String employeeUUID) {
        if (employeeUUID == null) {
            return null;
        }
        return employeeService.findEmployee(employeeUUID);
    }

    @Override
    public String getAsString(final FacesContext context,
            final UIComponent component,
            @Nullable final Object value) {
        if (!(value instanceof Employee)) {
            return null;
        }
        return ((Employee) value).getUuid();
    }
}
