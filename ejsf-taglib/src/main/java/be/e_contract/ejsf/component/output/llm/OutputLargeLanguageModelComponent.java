/*
 * Enterprise JSF project.
 *
 * Copyright 2024-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.llm;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import org.primefaces.component.api.Widget;

@FacesComponent(OutputLargeLanguageModelComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "ejsf", name = "dompurify/purify.min.js"),
    @ResourceDependency(library = "ejsf", name = "marked/marked.umd.js"),
    @ResourceDependency(library = "ejsf", name = "katex/katex.min.css"),
    @ResourceDependency(library = "ejsf", name = "katex/katex.min.js"),
    @ResourceDependency(library = "ejsf", name = "katex/auto-render.min.js"),
    @ResourceDependency(library = "ejsf", name = "marked-katex-extension/index.umd.js"),
    @ResourceDependency(library = "ejsf", name = "output-llm.js")
})
public class OutputLargeLanguageModelComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.outputLLMComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputLargeLanguageModelComponent() {
        setRendererType(OutputLargeLanguageModelRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public enum PropertyKeys {
        widgetVar,
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }
}
