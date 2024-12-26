/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.input.template;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(HtmlComponent.COMPONENT_TYPE)
public class HtmlComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.htmlComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public HtmlComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        tag,
        style,
    }

    public void setTag(String tag) {
        getStateHelper().put(PropertyKeys.tag, tag);
    }

    public String getTag() {
        return (String) getStateHelper().get(PropertyKeys.tag);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyle() {
        return (String) getStateHelper().get(PropertyKeys.style);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);

        ResponseWriter responseWriter = context.getResponseWriter();
        String tag = getTag();
        responseWriter.startElement(tag, this);
        String style = getStyle();
        if (null != style) {
            responseWriter.writeAttribute("style", style, "style");
        }
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        super.encodeEnd(context);

        ResponseWriter responseWriter = context.getResponseWriter();
        String tag = getTag();
        responseWriter.endElement(tag);
    }
}
