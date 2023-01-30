/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.i18n;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;

@FacesComponent(TranslationComponent.COMPONENT_TYPE)
public class TranslationComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.translation";

    public static final String COMPONENT_FAMILY = "ejsf";

    public TranslationComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        language,
    }

    public String getLanguage() {
        return (String) getStateHelper().eval(PropertyKeys.language, null);
    }

    public void setLanguage(String language) {
        getStateHelper().put(PropertyKeys.language, language);
    }
}
