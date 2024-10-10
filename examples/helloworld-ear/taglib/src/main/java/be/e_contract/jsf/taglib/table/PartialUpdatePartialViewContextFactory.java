package be.e_contract.jsf.taglib.table;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;

public class PartialUpdatePartialViewContextFactory extends PartialViewContextFactory {

    public PartialUpdatePartialViewContextFactory(PartialViewContextFactory wrapped) {
        super(wrapped);
    }

    @Override
    public PartialViewContext getPartialViewContext(FacesContext context) {
        PartialViewContextFactory wrappedFactory = getWrapped();
        PartialViewContext originalContext = wrappedFactory.getPartialViewContext(context);
        return new PartialUpdatePartialViewContext(originalContext);
    }
}
