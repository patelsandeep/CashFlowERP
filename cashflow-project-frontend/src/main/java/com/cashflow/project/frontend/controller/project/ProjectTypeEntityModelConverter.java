package com.cashflow.project.frontend.controller.project;

import com.google.common.base.Splitter;
import static java.lang.String.format;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * 
 */
@ApplicationScoped
@FacesConverter(forClass = ProjectTypeEntityModel.class)
public class ProjectTypeEntityModelConverter implements Converter {
    
    private static final Splitter COLON_SPLITTER = Splitter.on("::");

    @Override
    public Object getAsObject(final FacesContext context, final UIComponent component, @Nullable final String value) {
        return Optional
                .ofNullable(value)
                .map((v) -> v.trim())
                .filter((v) -> !v.isEmpty())
                .map((v) -> COLON_SPLITTER.splitToList(v))
                .map((v) -> {
                    final ProjectEntityType entityType = ProjectEntityType.valueOf(v.get(2));
                    return new ProjectTypeEntityModel(v.get(0), v.get(1), entityType);
                })
                .orElse(null);
    }

    @Override
    public String getAsString(final FacesContext context, final UIComponent component, @Nullable final Object value) {
        if (!(value instanceof ProjectTypeEntityModel)) {
            return null;
        }

        final ProjectTypeEntityModel be = (ProjectTypeEntityModel) value;
        return format("%s::%s::%s", be.getEntityUUID(), be.getEntityName(), be.getEntityType());
    }
    
}
