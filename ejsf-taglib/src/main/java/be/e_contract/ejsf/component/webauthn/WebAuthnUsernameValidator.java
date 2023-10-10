/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(WebAuthnUsernameValidator.VALIDATOR_ID)
public class WebAuthnUsernameValidator implements Validator, StateHolder {

    public static final String VALIDATOR_ID = "ejsf.webAuthnUsernameValidator";

    private boolean _transient;

    private ValueExpression messageValueExpression;

    private String mode;

    private ValueExpression credentialRepositoryValueExpression;

    public ValueExpression getMessage() {
        return this.messageValueExpression;
    }

    public void setMessage(ValueExpression messageValueExpression) {
        this.messageValueExpression = messageValueExpression;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public ValueExpression getCredentialRepository() {
        return this.credentialRepositoryValueExpression;
    }

    public void setCredentialRepository(ValueExpression credentialRepositoryValueExpression) {
        this.credentialRepositoryValueExpression = credentialRepositoryValueExpression;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (UIInput.isEmpty(value)) {
            return;
        }
        String username = (String) value;
        ELContext elContext = facesContext.getELContext();
        CredentialRepository credentialRepository = (CredentialRepository) this.credentialRepositoryValueExpression.getValue(elContext);
        Optional<ByteArray> userHandle = credentialRepository.getUserHandleForUsername(username);
        if ("registration".equals(this.mode)) {
            if (userHandle.isPresent()) {
                String message;
                if (null != this.messageValueExpression) {
                    message = (String) this.messageValueExpression.getValue(elContext);
                } else {
                    message = "Existing user.";
                }
                FacesMessage facesMessage = new FacesMessage(message);
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
            }
        } else if ("authentication".equals(this.mode)) {
            if (!userHandle.isPresent()) {
                String message;
                if (null != this.messageValueExpression) {
                    message = (String) this.messageValueExpression.getValue(elContext);
                } else {
                    message = "Unknown user.";
                }
                FacesMessage facesMessage = new FacesMessage(message);
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
            }
        } else {
            FacesMessage facesMessage = new FacesMessage("Unsupported mode.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.mode,
            this.credentialRepositoryValueExpression,
            this.messageValueExpression
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
        this.mode = (String) stateObjects[0];
        this.credentialRepositoryValueExpression = (ValueExpression) stateObjects[1];
        this.messageValueExpression = (ValueExpression) stateObjects[2];
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
