/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import java.io.File;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(HomeFileValidator.VALIDATOR_ID)
public class HomeFileValidator implements Validator, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeFileValidator.class);

    public static final String VALIDATOR_ID = "ejsf.homeFileValidator";

    private boolean _transient;

    private String directory;

    private String message;

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.directory,
            this.message
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
        this.directory = (String) stateObjects[0];
        this.message = (String) stateObjects[1];
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        this._transient = newTransientValue;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (null == value) {
            // allow for optional
            return;
        }
        String fileName = (String) value;
        if (fileName.isEmpty()) {
            // allow for optional
            return;
        }
        String normalizedFileName = FilenameUtils.normalize(fileName);
        if (!normalizedFileName.equals(fileName)) {
            LOGGER.warn("normalized filename different from filename: {}", fileName);
            throwValidatorException();
        }
        String userHome = System.getProperty("user.home");
        if (!fileName.startsWith(userHome)) {
            LOGGER.warn("file not in user home directory: {}", fileName);
            throwValidatorException();
        }
        if (null != this.directory) {
            String directoryPrefix = userHome + "/" + this.directory + "/";
            if (!fileName.startsWith(directoryPrefix)) {
                LOGGER.warn("file not in directory: {}", directoryPrefix);
                throwValidatorException();
            }
        }
        File file = new File(fileName);
        if (!file.exists()) {
            LOGGER.warn("file does not exist: {}", fileName);
            throwValidatorException();
        }
        if (!file.isFile()) {
            LOGGER.warn("not a regular file: {}", fileName);
            throwValidatorException();
        }
        if (!file.canRead()) {
            LOGGER.warn("cannot read file: {}", fileName);
            throwValidatorException();
        }
    }

    private void throwValidatorException() throws ValidatorException {
        String errorMessage;
        if (null != this.message) {
            errorMessage = this.message;
        } else {
            errorMessage = "Invalid filename.";
        }
        FacesMessage facesMessage = new FacesMessage(errorMessage);
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        throw new ValidatorException(facesMessage);
    }
}
