package be.e_contract.jsf.taglib.converter;

import java.io.IOException;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class GenericConverterTagHandler extends TagHandler {

    public GenericConverterTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof UIInput)) {
            throw new TagException(this.tag, "parent must be UIInput.");
        }
        TagAttribute getAsObjectTagAttribute = getRequiredAttribute("getAsObject");
        MethodExpression getAsObjectMethodExpression = getAsObjectTagAttribute.getMethodExpression(faceletContext, Object.class, new Class[]{String.class});
        TagAttribute getAsStringTagAttribute = getRequiredAttribute("getAsString");
        MethodExpression getAsStringMethodExpression = getAsStringTagAttribute.getMethodExpression(faceletContext, String.class, new Class[]{Object.class});

        UIInput input = (UIInput) parent;
        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();
        GenericConverter genericConverter
                = (GenericConverter) application.createConverter(
                        GenericConverter.CONVERTER_ID);
        genericConverter.setGetAsObjectMethodExpression(
                getAsObjectMethodExpression);
        genericConverter.setGetAsStringMethodExpression(
                getAsStringMethodExpression);
        input.setConverter(genericConverter);
    }
}
