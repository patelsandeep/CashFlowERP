package com.cashflow.project.frontend.controller.ownedequipment;

import com.cashflow.fixedasset.api.FixedAssetService;
import com.cashflow.fixedasset.domain.FixedAsset;
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
@FacesConverter(forClass = FixedAsset.class)
public class EquipmentConverter implements Converter {

    @Inject
    private FixedAssetService fixedAssetService;

    @Override
    public Object getAsObject(final FacesContext context,
                              final UIComponent component,
                              @Nullable final String fixedAssetUUID) {
        if (fixedAssetUUID == null) {
            return null;
        }

        return fixedAssetService.findByUUIDOrAssetIDOrSerialNumber(fixedAssetUUID);
    }

    @Override
    public String getAsString(final FacesContext context,
                              final UIComponent component,
                              @Nullable final Object value) {
        if (!(value instanceof FixedAsset)) {
            return null;
        }
        return ((FixedAsset) value).getUuid();
    }
}
