/*
 * Enterprise JSF project.
 *
 * Copyright 2020-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.countdown;

import javax.faces.application.Application;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.primefaces.component.api.Widget;
import org.primefaces.component.progressbar.ProgressBar;

@FacesComponent(CountdownComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "countdown.js"),
    @ResourceDependency(library = "ejsf", name = "countdown.css")
})
public class CountdownComponent extends UINamingContainer implements SystemEventListener, Widget {

    public static final String COMPONENT_TYPE = "ejsf.countdownComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public CountdownComponent() {
        setRendererType(CountdownRenderer.RENDERER_TYPE);
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        root.subscribeToViewEvent(PostAddToViewEvent.class, this);
    }

    enum PropertyKeys {
        widgetVar
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return source instanceof UIViewRoot;
    }

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        if (!(event instanceof PostAddToViewEvent)) {
            return;
        }
        FacesContext facesContext = getFacesContext();
        Application application = facesContext.getApplication();
        ProgressBar progressBar = (ProgressBar) application.createComponent(ProgressBar.COMPONENT_TYPE);
        progressBar.setId("progressBar");
        getChildren().add(progressBar);
    }
}
