/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.matomo;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = TrackingComponent.COMPONENT_FAMILY, rendererType = TrackingRenderer.RENDERER_TYPE)
public class TrackingRenderer extends CoreRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackingRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.matomoTrackingRenderer";

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        TrackingComponent trackingComponent = (TrackingComponent) component;
        if (!trackingComponent.isEnableTracking()) {
            return;
        }

        String matomoUrl = trackingComponent.getMatomoUrl();
        if (UIInput.isEmpty(matomoUrl)) {
            LOGGER.warn("missing Matomo URL config");
            return;
        }

        String matomoSiteId = trackingComponent.getSiteId();
        if (UIInput.isEmpty(matomoSiteId)) {
            LOGGER.warn("missing Matomo Site ID config");
            return;
        }

        String userId = trackingComponent.getUserId();
        String documentTitle = trackingComponent.getDocumentTitle();

        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("EJSFMatomoTracking", trackingComponent);
        widgetBuilder.attr("MATOMO_URL", matomoUrl);
        widgetBuilder.attr("SITE_ID", matomoSiteId);
        if (!UIInput.isEmpty(userId)) {
            widgetBuilder.attr("USER_ID", userId);
        }
        if (!UIInput.isEmpty(documentTitle)) {
            widgetBuilder.attr("DOCUMENT_TITLE", documentTitle);
        }
        List<CustomDimension> customDimensions = trackingComponent.getCustomDimensions();
        if (null != customDimensions) {
            widgetBuilder.attr("CUSTOM_DIMENSION_COUNT", customDimensions.size());
            for (int idx = 0; idx < customDimensions.size(); idx++) {
                CustomDimension customDimension = customDimensions.get(idx);
                widgetBuilder.attr("CUSTOM_DIMENSION_" + idx + "_DIMENSION", customDimension.getDimension());
                widgetBuilder.attr("CUSTOM_DIMENSION_" + idx + "_VALUE", customDimension.getValue());
            }
        }
        widgetBuilder.finish();
    }
}
