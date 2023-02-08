/*
 * Enterprise JSF project.
 *
 * Copyright 2020-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator("ejsf.urlValidator")
public class UrlValidator implements Validator, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlValidator.class);

    private boolean allowLocalhost;

    private boolean allowHttp;

    private boolean _transient;

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (UIInput.isEmpty(value)) {
            // allow for optional URL
            return;
        }
        String[] schemes;
        if (this.allowHttp) {
            schemes = new String[]{"http", "https"};
        } else {
            schemes = new String[]{"https"};
        }

        long options;
        if (this.allowLocalhost) {
            options = org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;
        } else {
            options = 0;
        }
        try {
            Class.forName("org.apache.commons.validator.routines.UrlValidator");
        } catch (ClassNotFoundException ex) {
            String errorMessage = "Missing commons-validator";
            LOGGER.error(errorMessage);
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        org.apache.commons.validator.routines.UrlValidator urlValidator = new org.apache.commons.validator.routines.UrlValidator(schemes, options);
        boolean valid = urlValidator.isValid((String) value);
        if (valid) {
            return;
        }
        LOGGER.warn("invalid URL: {}", value);
        Application application = facesContext.getApplication();
        ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
        String errorMessage = resourceBundle.getString("invalidUrl");
        FacesMessage facesMessage = new FacesMessage(errorMessage);
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        throw new ValidatorException(facesMessage);
    }

    public boolean isAllowLocalhost() {
        return this.allowLocalhost;
    }

    public void setAllowLocalhost(boolean allowLocalhost) {
        this.allowLocalhost = allowLocalhost;
    }

    public boolean isAllowHttp() {
        return this.allowHttp;
    }

    public void setAllowHttp(boolean allowHttp) {
        this.allowHttp = allowHttp;
    }

    @Override
    public Object saveState(FacesContext context) {
        LOGGER.trace("saveState(..)");
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{this.allowLocalhost, this.allowHttp};
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        LOGGER.trace("restoreState(..)");
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
        this.allowLocalhost = (Boolean) stateObjects[0];
        this.allowHttp = (Boolean) stateObjects[1];
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
