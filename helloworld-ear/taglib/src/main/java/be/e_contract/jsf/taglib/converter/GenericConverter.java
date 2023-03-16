package be.e_contract.jsf.taglib.converter;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(GenericConverter.CONVERTER_ID)
public class GenericConverter implements Converter<Object>, StateHolder {

    public static final String CONVERTER_ID = "ejsf.genericConverter";

    private MethodExpression getAsObjectMethodExpression;

    private MethodExpression getAsStringMethodExpression;

    private boolean _transient;

    public void setGetAsObjectMethodExpression(MethodExpression getAsObjectMethodExpression) {
        this.getAsObjectMethodExpression = getAsObjectMethodExpression;
    }

    public void setGetAsStringMethodExpression(MethodExpression getAsStringMethodExpression) {
        this.getAsStringMethodExpression = getAsStringMethodExpression;
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (null == value) {
            return null;
        }
        ELContext elContext = facesContext.getELContext();
        return this.getAsObjectMethodExpression.invoke(elContext, new Object[]{value});
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (null == value) {
            return null;
        }
        ELContext elContext = facesContext.getELContext();
        return (String) this.getAsStringMethodExpression.invoke(elContext, new Object[]{value});
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.getAsObjectMethodExpression,
            this.getAsStringMethodExpression
        };
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (state == null) {
            return;
        }
        Object[] stateObjects = (Object[]) state;
        if (stateObjects.length == 0) {
            return;
        }
        this.getAsObjectMethodExpression = (MethodExpression) stateObjects[0];
        this.getAsStringMethodExpression = (MethodExpression) stateObjects[1];
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        this._transient = newTransientValue;
    }
}
