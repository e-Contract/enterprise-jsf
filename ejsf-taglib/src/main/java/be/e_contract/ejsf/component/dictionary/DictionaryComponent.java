/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.dictionary;

import java.util.Map;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import org.primefaces.component.api.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(DictionaryComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "dictionary.js")
})
public class DictionaryComponent extends UIComponentBase implements Widget {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.dictionaryComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        messages,
        widgetVar,
    }

    public Map<String, String> getMessages() {
        return (Map<String, String>) getStateHelper().eval(PropertyKeys.messages);
    }

    public void setMessages(Map<String, String> messages) {
        getStateHelper().put(PropertyKeys.messages, messages);
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }
}
