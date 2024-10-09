package be.e_contract.jsf.taglib.table;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(TableJavascriptUpdateComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "ejsf-table.css"),
    @ResourceDependency(library = "javax.faces", name = "jsf.js"),
    @ResourceDependency(library = "ejsf", name = "ejsf-widget.js"),
    @ResourceDependency(library = "ejsf", name = "ejsf-table-js-update.js")
})
public class TableJavascriptUpdateComponent extends UIOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableJavascriptUpdateComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.tableJavascriptUpdateComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public TableJavascriptUpdateComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        pageIdx
    }

    public int getPageIdx() {
        return (int) getStateHelper().eval(PropertyKeys.pageIdx, 0);
    }

    public void setPageIdx(int pageIdx) {
        getStateHelper().put(PropertyKeys.pageIdx, pageIdx);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        TableModel tableModel = (TableModel) getValue();
        ResponseWriter responseWriter = context.getResponseWriter();
        HTMLEncoder html = new HTMLEncoder(responseWriter);
        html.element("table", () -> {
            String clientId = super.getClientId(context);
            html.attr("id", clientId);
            html.attr("class", "ejsf-table");
            html.attr("data-ejsf-widget-type", "TableJavascriptUpdateWidget");
            html.attr("data-ejsf-widget-update", 1);
            html.element("thead", () -> {
                html.element("tr", () -> {
                    html.element("th", () -> {
                        html.element("button", () -> {
                            html.attr("type", "button");
                            html.attr("class", "ejsf-table-button-prev");
                            html.content("Previous");
                        });
                        html.element("button", () -> {
                            html.attr("type", "button");
                            html.attr("class", "ejsf-table-button-next");
                            html.content("Next");
                        });
                    });
                });
            });
            html.element("tbody", () -> {
                int pageIdx = getPageIdx();
                List<String> dataList = tableModel.getPage(pageIdx);
                AtomicInteger pageRowIdx = new AtomicInteger(0);
                for (String data : dataList) {
                    html.element("tr", () -> {
                        html.element("td", () -> {
                            html.attr("data-ejsf-table-page-row", pageRowIdx);
                            pageRowIdx.incrementAndGet();
                            html.content(data);
                        });
                    });
                }
            });
        });
    }

    private void update(FacesContext facesContext) {
        String clientId = getClientId();
        String script = "ejsf.TableJavascriptUpdateWidget.update(\"" + clientId + "\",[";
        TableModel tableModel = (TableModel) getValue();
        int pageIdx = getPageIdx();
        List<String> dataList = tableModel.getPage(pageIdx);
        for (String data : dataList) {
            script += "\"" + data + "\",";
        }
        script += "]);";
        PartialViewContext partialViewContext = facesContext.getPartialViewContext();
        List<String> evalScripts = partialViewContext.getEvalScripts();
        evalScripts.add(script);
    }

    @Override
    public void decode(FacesContext context) {
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> params = externalContext.getRequestParameterMap();
        String behaviorSource = params.get(
                ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        if (null == behaviorSource) {
            return;
        }
        String clientId = getClientId(context);
        if (!behaviorSource.equals(clientId)) {
            return;
        }
        String behaviorEvent = params.get(
                ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (null == behaviorEvent) {
            return;
        }
        if ("next".equals(behaviorEvent)) {
            int pageIdx = getPageIdx();
            pageIdx++;
            TableModel tableModel = (TableModel) getValue();
            if (pageIdx < tableModel.getPageCount()) {
                setPageIdx(pageIdx);
            }
            update(context);
            return;
        }
        if ("prev".equals(behaviorEvent)) {
            int pageIdx = getPageIdx();
            pageIdx--;
            if (pageIdx >= 0) {
                setPageIdx(pageIdx);
            }
            update(context);
            return;
        }
        LOGGER.error("unknown event: {}", behaviorEvent);
    }
}
