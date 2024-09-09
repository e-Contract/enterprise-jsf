/*
 * Enterprise JSF project.
 *
 * Copyright 2015-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@FacesValidator(XMLValidator.VALIDATOR_ID)
public class XMLValidator implements Validator, PartialStateHolder {

    public static final String VALIDATOR_ID = "ejsf.xmlValidator";

    private boolean _transient;

    private String message;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.initialState = false;
        this.message = message;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }
        String strValue = (String) value;
        if (strValue.isEmpty()) {
            return;
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);
        DocumentBuilder documentBuilder;
        try {
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            return;
        }
        XMLValidatorErrorHandler errorHandler = new XMLValidatorErrorHandler();
        documentBuilder.setErrorHandler(errorHandler);

        try {
            documentBuilder.parse(new InputSource(new StringReader(strValue)));
        } catch (SAXException | IOException ex) {
            String errorMessage;
            if (null != this.message) {
                errorMessage = this.message;
            } else {
                Application application = facesContext.getApplication();
                ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
                errorMessage = resourceBundle.getString("invalidXml");
            }
            errorMessage = MessageFormat.format(errorMessage, errorHandler.getLineNumber(), errorHandler.getColumnNumber());
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    private static class XMLValidatorErrorHandler implements ErrorHandler {

        private static final Logger LOGGER = LoggerFactory.getLogger(XMLValidatorErrorHandler.class);

        private int lineNumber;

        private int columnNumber;

        private void storeLocation(SAXParseException ex) {
            this.lineNumber = ex.getLineNumber();
            this.columnNumber = ex.getColumnNumber();
        }

        public int getLineNumber() {
            return this.lineNumber;
        }

        public int getColumnNumber() {
            return this.columnNumber;
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            LOGGER.warn("XML warning: {} {}", exception.getLineNumber(), exception.getColumnNumber());
            storeLocation(exception);
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            LOGGER.error("XML error: {} {}", exception.getLineNumber(), exception.getColumnNumber());
            storeLocation(exception);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            LOGGER.error("XML fatal error: {} {}", exception.getLineNumber(), exception.getColumnNumber());
            storeLocation(exception);
        }
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
        this.message = (String) stateObjects[0];
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
