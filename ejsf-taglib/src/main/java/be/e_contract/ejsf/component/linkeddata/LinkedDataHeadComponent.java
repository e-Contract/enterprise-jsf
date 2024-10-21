/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.linkeddata;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(LinkedDataHeadComponent.COMPONENT_TYPE)
public class LinkedDataHeadComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.linkedDataHeadComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public LinkedDataHeadComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        contentList,
    }

    public void addContent(String content) {
        List<String> contentList = (List<String>) getStateHelper().get(PropertyKeys.contentList);
        if (null == contentList) {
            contentList = new LinkedList<>();
            getStateHelper().put(PropertyKeys.contentList, contentList);
        }
        contentList.add(content);
    }

    private List<String> getContentList() {
        List<String> contentList = (List<String>) getStateHelper().get(PropertyKeys.contentList);
        if (null == contentList) {
            return new LinkedList<>();
        }
        return contentList;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("script", null);
        responseWriter.writeAttribute("type", "application/ld+json", null);
        List<String> contentList = getContentList();
        for (String content : contentList) {
            responseWriter.write(content);
        }
        responseWriter.endElement("script");
    }
}
