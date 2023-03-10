/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.dictionary;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = DictionaryComponent.COMPONENT_FAMILY, rendererType = DictionaryRenderer.RENDERER_TYPE)
public class DictionaryRenderer extends CoreRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.dictionaryRenderer";

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        DictionaryComponent dictionaryComponent = (DictionaryComponent) component;
        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("EJSFDictionary", dictionaryComponent);
        Map<String, String> messages = dictionaryComponent.getMessages();
        for (Map.Entry<String, String> message : messages.entrySet()) {
            widgetBuilder.attr("message_" + message.getKey(), message.getValue());
        }
        widgetBuilder.finish();
    }
}
