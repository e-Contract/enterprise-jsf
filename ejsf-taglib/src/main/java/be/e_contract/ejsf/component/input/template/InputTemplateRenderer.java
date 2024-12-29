/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.input.template;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = InputTemplateComponent.COMPONENT_FAMILY,
        rendererType = InputTemplateRenderer.RENDERER_TYPE)
public class InputTemplateRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.inputTemplateRenderer";

    private static final Logger LOGGER = LoggerFactory.getLogger(InputTemplateRenderer.class);

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        InputTemplateComponent inputTemplateComponent = (InputTemplateComponent) component;
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.startElement("div", inputTemplateComponent);
        String clientId = inputTemplateComponent.getClientId(facesContext);
        responseWriter.writeAttribute("id", clientId, "id");
        {
            responseWriter.startElement("input", null);
            responseWriter.writeAttribute("type", "hidden", null);
            responseWriter.writeAttribute("name", clientId + ":input", null);
            String value = (String) inputTemplateComponent.getSubmittedValue();
            if (null == value) {
                value = (String) inputTemplateComponent.getValue();
            }
            if (null != value) {
                responseWriter.writeAttribute("value", value, "value");
            }
            responseWriter.endElement("input");
        }
        {
            responseWriter.startElement("div", null);
            responseWriter.writeAttribute("id", clientId + ":content", null);
            responseWriter.endElement("div");
        }
        responseWriter.endElement("div");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFInputTemplate", inputTemplateComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> requestParameterMap
                = externalContext.getRequestParameterMap();
        InputTemplateComponent inputTemplateComponent = (InputTemplateComponent) component;
        String clientId = inputTemplateComponent.getClientId(facesContext);
        String submittedValue
                = (String) requestParameterMap.get(clientId + ":input");
        if (null != submittedValue) {
            LOGGER.debug("submitted value: {}", submittedValue);
            inputTemplateComponent.setSubmittedValue(submittedValue);
            inputTemplateComponent.setValid(true);
        }
    }
}
