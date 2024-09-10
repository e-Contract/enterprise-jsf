package be.e_contract.jsf.taglib.validator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator("ejsf.examplePSHValidator")
public class ExamplePartialStateHolderValidator implements Validator, PartialStateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExamplePartialStateHolderValidator.class);

    private boolean _transient;

    private String forbidden;

    private String message;

    public String getForbidden() {
        return this.forbidden;
    }

    public void setForbidden(String forbidden) {
        LOGGER.debug("setForbidden: {}", forbidden);
        this.initialState = false;
        this.forbidden = forbidden;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        LOGGER.debug("setMessage: {}", message);
        this.initialState = false;
        this.message = message;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (null == value) {
            return;
        }
        if (this.forbidden.equals(value)) {
            String errorMessage;
            if (null != this.message) {
                errorMessage = this.message;
            } else {
                errorMessage = "Not allowed: " + this.forbidden;
            }
            FacesMessage facesMessage
                    = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            errorMessage, null);
            throw new ValidatorException(facesMessage);
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        LOGGER.debug("saveState");
        if (context == null) {
            throw new NullPointerException();
        }
        if (this.initialState) {
            LOGGER.debug("initial state");
            return null;
        }
        Object state = new Object[]{
            this.forbidden,
            this.message
        };
        logObjectSize(state);
        return state;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        LOGGER.debug("restoreState");
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
        this.forbidden = (String) stateObjects[0];
        this.message = (String) stateObjects[1];
    }

    private void logObjectSize(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos)) {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
        } catch (IOException ex) {
            return;
        }
        LOGGER.debug("object size: {} bytes", baos.size());
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        this._transient = newTransientValue;
    }

    private boolean initialState;

    @Override
    public void markInitialState() {
        LOGGER.debug("markInitialState");
        this.initialState = true;
    }

    @Override
    public boolean initialStateMarked() {
        LOGGER.debug("initialStateMarked: {}", this.initialState);
        return this.initialState;
    }

    @Override
    public void clearInitialState() {
        LOGGER.debug("clearInitialState");
        this.initialState = false;
    }
}
