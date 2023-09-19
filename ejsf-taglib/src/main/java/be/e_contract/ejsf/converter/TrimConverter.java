/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(TrimConverter.CONVERTER_ID)
public class TrimConverter implements Converter {

    public static final String CONVERTER_ID = "ejsf.trimConverter";

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (context == null || component == null) {
            throw new NullPointerException();
        }
        if (null == value) {
            return null;
        }
        value = value.trim();
        if (value.isEmpty()) {
            return null;
        }
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (context == null || component == null) {
            throw new NullPointerException();
        }
        if (null == value) {
            return "";
        }
        String strValue = (String) value;
        return strValue;
    }
}
