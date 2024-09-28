/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.console;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import org.primefaces.component.api.Widget;

@FacesComponent(ConsoleComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "console/console.js"),
    @ResourceDependency(library = "ejsf", name = "console/console.css")
})
public class ConsoleComponent extends UIComponentBase implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.consoleComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ConsoleComponent() {
        setRendererType(ConsoleRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        widgetVar,
        style,
        styleClass,
        timestamp
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public boolean isTimestamp() {
        return (boolean) getStateHelper().eval(PropertyKeys.timestamp, false);
    }

    public void setTimestamp(boolean timestamp) {
        getStateHelper().put(PropertyKeys.timestamp, timestamp);
    }
}
