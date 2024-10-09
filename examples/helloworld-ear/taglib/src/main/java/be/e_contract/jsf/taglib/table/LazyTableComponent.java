package be.e_contract.jsf.taglib.table;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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

@FacesComponent(LazyTableComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "ejsf-table.css"),
    @ResourceDependency(library = "javax.faces", name = "jsf.js"),
    @ResourceDependency(library = "ejsf", name = "ejsf-widget.js"),
    @ResourceDependency(library = "ejsf", name = "ejsf-table-lazy.js")
})
public class LazyTableComponent extends UIOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(LazyTableComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.lazyTableComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public LazyTableComponent() {
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
            html.attr("data-ejsf-widget-type", "LazyTableWidget");
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
            PartialViewContext partialViewContext = context.getPartialViewContext();
            if (partialViewContext.isAjaxRequest()) {
                html.element("tbody", () -> {
                    int pageIdx = getPageIdx();
                    List<String> dataList = tableModel.getPage(pageIdx);
                    for (String data : dataList) {
                        html.element("tr", () -> {
                            html.element("td", () -> {
                                html.content(data);
                            });
                        });
                    }
                });
            }
        });
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
            return;
        }
        if ("prev".equals(behaviorEvent)) {
            int pageIdx = getPageIdx();
            pageIdx--;
            if (pageIdx >= 0) {
                setPageIdx(pageIdx);
            }
            return;
        }
        if ("load".equals(behaviorEvent)) {
            LOGGER.debug("lazy load event received");
            return;
        }
        LOGGER.error("unknown event: {}", behaviorEvent);
    }
}
