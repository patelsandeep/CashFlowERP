package com.cashflow.project.frontend.controller.supervisor;

import com.google.common.base.Splitter;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import static java.lang.String.format;

/**
 *
 * 
 */
@ApplicationScoped
@FacesConverter(forClass = SupervisorEntityModel.class)
public class SupervisorEntityModelConverter implements Converter {

    private static final Splitter COLON_SPLITTER = Splitter.on("::");

    @Override
    public Object getAsObject(final FacesContext context, final UIComponent component, @Nullable final String value) {
        return Optional
                .ofNullable(value)
                .map((v) -> v.trim())
                .filter((v) -> !v.isEmpty())
                .map((v) -> COLON_SPLITTER.splitToList(v))
                .map((v) -> {
                    final SupervisorEntityType entityType = SupervisorEntityType.valueOf(v.get(2));
                    return new SupervisorEntityModel(v.get(0), v.get(1), entityType);
                })
                .orElse(null);
    }

    @Override
    public String getAsString(final FacesContext context, final UIComponent component, @Nullable final Object value) {
        if (!(value instanceof SupervisorEntityModel)) {
            return null;
        }

        final SupervisorEntityModel be = (SupervisorEntityModel) value;
        return format("%s::%s::%s", be.getSupervisorUUID(), be.getSupervisorName(), be.getPhaseSupervisorEntityType());
    }

}
