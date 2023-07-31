package be.e_contract.jsf.taglib.behavior;

import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.FacesBehavior;

@FacesBehavior(ExampleClientBehavior.BEHAVIOR_ID)
public class ExampleClientBehavior extends ClientBehaviorBase {

    public static final String BEHAVIOR_ID = "exampleClientBehavior";

    @Override
    public String getRendererType() {
        return ExampleClientBehaviorRenderer.RENDERER_TYPE;
    }
}
