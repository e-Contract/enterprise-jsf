/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.link;

import java.io.IOException;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(LinkHeadComponent.COMPONENT_TYPE)
public class LinkHeadComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.linkHeadComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public LinkHeadComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        href,
        rel
    }

    public void setHref(ValueExpression href) {
        getStateHelper().put(PropertyKeys.href, href);
    }

    public ValueExpression getHref() {
        return (ValueExpression) getStateHelper().eval(PropertyKeys.href);
    }

    public void setRel(String rel) {
        getStateHelper().put(PropertyKeys.rel, rel);
    }

    public String getRel() {
        return (String) getStateHelper().eval(PropertyKeys.rel);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("link", this);
        ValueExpression hrefValueExpression = getHref();
        ELContext elContext = context.getELContext();
        String href = (String) hrefValueExpression.getValue(elContext);
        responseWriter.writeAttribute("href", href, "href");
        String rel = getRel();
        if (null != rel) {
            responseWriter.writeAttribute("rel", rel, "rel");
        }
        responseWriter.endElement("link");
    }
}
