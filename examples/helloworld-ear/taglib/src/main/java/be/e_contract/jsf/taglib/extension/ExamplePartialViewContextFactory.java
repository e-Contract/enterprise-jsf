package be.e_contract.jsf.taglib.extension;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;

public class ExamplePartialViewContextFactory extends PartialViewContextFactory {

    public ExamplePartialViewContextFactory(PartialViewContextFactory wrapped) {
        super(wrapped);
    }

    @Override
    public PartialViewContext getPartialViewContext(FacesContext context) {
        PartialViewContextFactory wrappedFactory = getWrapped();
        PartialViewContext originalContext = wrappedFactory.getPartialViewContext(context);
        return new ExamplePartialViewContext(originalContext);
    }
}
