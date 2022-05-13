package be.e_contract.jsf.taglib;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.FacesBehaviorRenderer;

@FacesBehaviorRenderer(rendererType = ExampleClientBehaviorRenderer.RENDERER_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "example-client-behavior.js", target = "head"),
    @ResourceDependency(library = "ejsf", name = "example-client-behavior.css", target = "head")
})
public class ExampleClientBehaviorRenderer extends ClientBehaviorRenderer {

    public static final String RENDERER_TYPE = "exampleClientBehaviorRenderer";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior clientBehavior) {
        return "exampleClientBehavior(event);";
    }
}
