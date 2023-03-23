/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.test;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.PrimeFaces;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = TestComponentComponent.COMPONENT_FAMILY, rendererType = TestComponentRenderer.RENDERER_TYPE)
public class TestComponentRenderer extends CoreRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestComponentRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.testComponentRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = component.getClientId(facesContext);
        responseWriter.startElement("div", component);
        responseWriter.writeAttribute("id", clientId, "id");
        LOGGER.debug("number of children: {}", component.getChildCount());
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.endElement("div");

        responseWriter.startElement("textarea", null);
        String clientId = component.getClientId(facesContext);
        responseWriter.writeAttribute("id", clientId + "_log", null);
        responseWriter.writeAttribute("cols", 80, null);
        responseWriter.writeAttribute("rows", 20, null);
        responseWriter.writeAttribute("readonly", "readonly", null);
        responseWriter.endElement("textarea");

        TestComponentComponent testComponentComponent = (TestComponentComponent) component;
        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFTestComponent", testComponentComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String clientId = component.getClientId(context);
        if (requestParameterMap.containsKey(clientId + "_request_test")) {
            LOGGER.debug("requesting test");
            TestComponentComponent testComponentComponent = (TestComponentComponent) component;
            ComponentTest componentTest = testComponentComponent.getNextTest();
            PrimeFaces primeFaces = PrimeFaces.current();
            PrimeFaces.Ajax ajax = primeFaces.ajax();
            if (null != componentTest) {
                String testName = componentTest.getName();
                LOGGER.debug("running test: {}", testName);
                UIComponent componentUnderTest = testComponentComponent.getChildren().get(0);
                componentTest.run(testComponentComponent, componentUnderTest);
                ajax.addCallbackParam("name", testName);
                LOGGER.debug("child count: {}", testComponentComponent.getChildCount());
                ajax.update(componentUnderTest);
            } else {
                LOGGER.debug("done testing");
                ajax.addCallbackParam("doneTesting", true);
            }
            context.renderResponse();
        }
    }
}
