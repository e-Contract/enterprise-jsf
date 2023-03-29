package be.e_contract.jsf.taglib;

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

@FacesRenderer(componentFamily = EChartsComponent.COMPONENT_FAMILY,
        rendererType = EChartsRenderer.RENDERER_TYPE)
public class EChartsRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.echartsRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        EChartsComponent eChartsComponent = (EChartsComponent) component;
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = eChartsComponent.getClientId(facesContext);

        responseWriter.startElement("div", eChartsComponent);
        responseWriter.writeAttribute("id", clientId, "id");
        int width = eChartsComponent.getWidth();
        int height = eChartsComponent.getHeight();
        String style = "width: " + width + "px; height: " + height + "px;";
        responseWriter.writeAttribute("style", style, null);
        responseWriter.endElement("div");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EChartsWidget", eChartsComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        EChartsComponent eChartsComponent = (EChartsComponent) component;
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String clientId = component.getClientId(context);
        if (!requestParameterMap.containsKey(clientId)) {
            return;
        }
        String value = (String) eChartsComponent.getValue();
        PrimeFaces.current().ajax().addCallbackParam("option", value);
        context.renderResponse();
    }
}
