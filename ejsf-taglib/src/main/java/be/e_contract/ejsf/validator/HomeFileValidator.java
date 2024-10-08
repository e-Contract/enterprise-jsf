/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import java.io.File;
import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(HomeFileValidator.VALIDATOR_ID)
public class HomeFileValidator implements Validator, PartialStateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeFileValidator.class);

    public static final String VALIDATOR_ID = "ejsf.homeFileValidator";

    private boolean _transient;

    private String directory;

    private String message;

    private String type;

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.initialState = false;
        this.directory = directory;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.initialState = false;
        this.message = message;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.initialState = false;
        this.type = type;
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (this.initialState) {
            return null;
        }
        return new Object[]{
            this.directory,
            this.type,
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
        this.type = (String) stateObjects[1];
        this.message = (String) stateObjects[2];
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
        if (this.type != null && this.type.equals("DIRECTORY")) {
            if (!file.isDirectory()) {
                LOGGER.warn("not a directory file: {}", fileName);
                throwValidatorException();
            }
        } else {
            if (!file.isFile()) {
                LOGGER.warn("not a regular file: {}", fileName);
                throwValidatorException();
            }
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

    private boolean initialState;

    @Override
    public void markInitialState() {
        this.initialState = true;
    }

    @Override
    public boolean initialStateMarked() {
        return this.initialState;
    }

    @Override
    public void clearInitialState() {
        this.initialState = false;
    }
}
