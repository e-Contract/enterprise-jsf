package be.e_contract.jsf.taglib.output;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;

@FacesComponent(ExampleStyledOutput.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "example-styled-output.css")
})
public class ExampleStyledOutput extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.exampleStyledOutput";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ExampleStyledOutput() {
        setRendererType(ExampleStyledOutputRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}
