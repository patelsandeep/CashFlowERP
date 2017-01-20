package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
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
 * @since 6 Dec, 2016, 3:41:04 PM
 */
@ApplicationScoped
@FacesConverter(forClass = MemberEntityModel.class)
public class MemberEntityModelConvertor implements Converter {

    private static final Splitter COLON_SPLITTER = Splitter.on("::");

    @Override
    public Object getAsObject(final FacesContext context, final UIComponent component, @Nullable final String value) {
        return Optional
                .ofNullable(value)
                .map((v) -> v.trim())
                .filter((v) -> !v.isEmpty())
                .map((v) -> COLON_SPLITTER.splitToList(v))
                .map((v) -> {
                    final TeamMemberCategory entityType = TeamMemberCategory.valueOfEnum(v.get(2));
                    return new MemberEntityModel(v.get(0), v.get(1), entityType);
                })
                .orElse(null);
    }

    @Override
    public String getAsString(final FacesContext context, final UIComponent component, @Nullable final Object value) {
        if (!(value instanceof MemberEntityModel)) {
            return null;
        }

        final MemberEntityModel be = (MemberEntityModel) value;
        return format("%s::%s::%s", be.getUuid(), be.getName(), be.getMemberCategory());
    }

}
