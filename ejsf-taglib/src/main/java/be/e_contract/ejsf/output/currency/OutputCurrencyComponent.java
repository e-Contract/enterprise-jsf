/*
 * Enterprise JSF project.
 *
 * Copyright 2014-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output.currency;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import org.primefaces.component.api.Widget;

@FacesComponent(OutputCurrencyComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "output-currency.js")
})
public class OutputCurrencyComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.outputCurrency";

    public static final String COMPONENT_FAMILY = "ejsf";

    public enum PropertyKeys {
        widgetVar,
        currency,
        locale,
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getCurrency() {
        return (String) getStateHelper().eval(PropertyKeys.currency, "EUR");
    }

    public void setCurrency(String widgetVar) {
        getStateHelper().put(PropertyKeys.currency, widgetVar);
    }

    public Object getLocale() {
        return getStateHelper().eval(PropertyKeys.locale);
    }

    public void setLocale(Object locale) {
        getStateHelper().put(PropertyKeys.locale, locale);
    }
}
