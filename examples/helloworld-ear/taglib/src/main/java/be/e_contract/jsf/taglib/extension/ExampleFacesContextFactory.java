package be.e_contract.jsf.taglib.extension;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

public class ExampleFacesContextFactory extends FacesContextFactory {

    public ExampleFacesContextFactory(FacesContextFactory wrapped) {
        super(wrapped);
    }

    @Override
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle) throws FacesException {
        FacesContext wrappedContext = getWrapped().getFacesContext(context, request, response, lifecycle);
        if (wrappedContext instanceof ExampleFacesContextWrapper) {
            return wrappedContext;
        }
        FacesContext facesContext = new ExampleFacesContextWrapper(wrappedContext);
        return facesContext;
    }
}
