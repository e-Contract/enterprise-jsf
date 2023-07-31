package be.e_contract.jsf.taglib.behavior;

import java.io.IOException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class ExampleClientBehaviorTagHandler extends TagHandler {

    public ExampleClientBehaviorTagHandler(BehaviorConfig config) {
        super(config);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof ClientBehaviorHolder)) {
            throw new TagException(this.tag, "parent must be ClientBehaviorHolder.");
        }
        ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();
        ExampleClientBehavior exampleClientBehavior
                = (ExampleClientBehavior) application.createBehavior(
                        ExampleClientBehavior.BEHAVIOR_ID);
        clientBehaviorHolder.addClientBehavior("mouseover", exampleClientBehavior);
        clientBehaviorHolder.addClientBehavior("mouseout", exampleClientBehavior);
    }
}
