/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(VulnerableComponent.COMPONENT_TYPE)
public class VulnerableComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(VulnerableComponent.class);

    public static final String COMPONENT_TYPE = "demo.vulnerableComponent";

    public static final String COMPONENT_FAMILY = "demo";

    public VulnerableComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        secure,
        vulnerable,
    }

    public String getSecure() {
        return (String) getStateHelper().eval(PropertyKeys.secure);
    }

    public void setSecure(String secure) {
        getStateHelper().put(PropertyKeys.secure, secure);
    }

    public String getVulnerable() {
        return (String) getStateHelper().eval(PropertyKeys.vulnerable);
    }

    public void setVulnerable(String vulnerable) {
        getStateHelper().put(PropertyKeys.vulnerable, vulnerable);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        responseWriter.startElement("div", this);
        responseWriter.writeAttribute("id", clientId, "id");

        responseWriter.startElement("p", null);
        String secure = getSecure();
        LOGGER.debug("secure attribute: {}", secure);
        if (null != secure) {
            responseWriter.writeText(secure, "secure");
        }
        responseWriter.endElement("p");

        responseWriter.startElement("p", null);
        String vulnerable = getVulnerable();
        LOGGER.debug("vulnerable attribute: {}", vulnerable);
        if (null != vulnerable) {
            // no proper HTML escaping here
            responseWriter.write(vulnerable);
        }
        responseWriter.endElement("p");

        responseWriter.endElement("div");
    }
}
