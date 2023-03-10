/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.i18n;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(InlineTranslationComponent.COMPONENT_TYPE)
public class InlineTranslationComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(InlineTranslationComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.inlineTranslation";

    public static final String COMPONENT_FAMILY = "ejsf";

    public InlineTranslationComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        UIViewRoot viewRoot = context.getViewRoot();
        Locale locale = viewRoot.getLocale();
        String language = locale.getLanguage();
        List<UIComponent> children = getChildren();
        if (children.isEmpty()) {
            LOGGER.warn("missing Translation children");
            return;
        }
        TranslationComponent translationComponent = null;
        for (UIComponent child : children) {
            if (child instanceof TranslationComponent) {
                TranslationComponent candidateTranslationComponent = (TranslationComponent) child;
                if (language.equals(candidateTranslationComponent.getLanguage())) {
                    translationComponent = candidateTranslationComponent;
                    break;
                }
            }
        }
        if (null == translationComponent) {
            for (UIComponent child : children) {
                if (child instanceof TranslationComponent) {
                    translationComponent = (TranslationComponent) child;
                    break;
                }
            }
        }
        if (null == translationComponent) {
            LOGGER.warn("missing Translation children");
            return;
        }
        translationComponent.encodeChildren(context);
    }
}
