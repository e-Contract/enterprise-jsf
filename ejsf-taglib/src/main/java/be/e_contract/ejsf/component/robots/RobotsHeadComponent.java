/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.robots;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(RobotsHeadComponent.COMPONENT_TYPE)
public class RobotsHeadComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.robotsHeadComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public RobotsHeadComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        index,
        follow
    }

    public void setIndex(boolean index) {
        getStateHelper().put(PropertyKeys.index, index);
    }

    public boolean isIndex() {
        return (boolean) getStateHelper().eval(PropertyKeys.index, false);
    }

    public void setFollow(boolean follow) {
        getStateHelper().put(PropertyKeys.follow, follow);
    }

    public boolean isFollow() {
        return (boolean) getStateHelper().eval(PropertyKeys.follow, false);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("meta", this);
        responseWriter.writeAttribute("name", "robots", null);
        StringBuilder stringBuffer = new StringBuilder();
        if (isIndex()) {
            stringBuffer.append("index, ");
        } else {
            stringBuffer.append("noindex, ");
        }
        if (isFollow()) {
            stringBuffer.append("follow");
        } else {
            stringBuffer.append("nofollow");
        }
        responseWriter.writeAttribute("content", stringBuffer.toString(), null);
        responseWriter.endElement("meta");
    }
}
