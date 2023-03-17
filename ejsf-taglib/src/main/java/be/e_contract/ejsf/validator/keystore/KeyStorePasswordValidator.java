/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.keystore;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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

@FacesValidator(KeyStorePasswordValidator.VALIDATOR_ID)
public class KeyStorePasswordValidator implements Validator, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyStorePasswordValidator.class);

    public static final String VALIDATOR_ID = "ejsf.keyStorePasswordValidator";

    private boolean _transient;

    private String forKeyStore;

    private String keyStoreType;

    private String message;

    public String getForKeyStore() {
        return this.forKeyStore;
    }

    public void setForKeyStore(String forKeyStore) {
        this.forKeyStore = forKeyStore;
    }

    public String getKeyStoreType() {
        return this.keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
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
            this.forKeyStore,
            this.keyStoreType,
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
        this.forKeyStore = (String) stateObjects[0];
        this.keyStoreType = (String) stateObjects[1];
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
            return;
        }
        String keyStorePassword = (String) value;
        if (keyStorePassword.isEmpty()) {
            return;
        }
        UIInput keyStoreInput = (UIInput) component.findComponent(this.forKeyStore);
        if (null == keyStoreInput) {
            LOGGER.error("component not found: {}", this.forKeyStore);
            FacesMessage facesMessage = new FacesMessage("Config error.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        String keyStorePath = (String) keyStoreInput.getValue();
        if (null == keyStorePath) {
            return;
        }
        if (keyStorePath.isEmpty()) {
            return;
        }
        try {
            KeyStore keyStore = KeyStore.getInstance(this.keyStoreType);
            try (FileInputStream fis = new FileInputStream(keyStorePath)) {
                keyStore.load(fis, keyStorePassword.toCharArray());
            } catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
                throwValidatorException();
            }
        } catch (KeyStoreException ex) {
            throwValidatorException();
        }
    }

    private void throwValidatorException() throws ValidatorException {
        String errorMessage;
        if (null != this.message) {
            errorMessage = this.message;
        } else {
            errorMessage = "Keystore error.";
        }
        FacesMessage facesMessage = new FacesMessage(errorMessage);
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        throw new ValidatorException(facesMessage);
    }
}
