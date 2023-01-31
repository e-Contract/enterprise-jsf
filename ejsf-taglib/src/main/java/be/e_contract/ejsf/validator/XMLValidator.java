/*
 * Enterprise JSF project.
 *
 * Copyright 2015-2020 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import java.io.IOException;
import java.io.StringReader;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
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

@FacesValidator("ejsf.xmlValidator")
public class XMLValidator implements Validator {

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
            Application application = facesContext.getApplication();
            ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
            String message = resourceBundle.getString("invalidXml") + " (" + errorHandler.getLineNumber() + "," + errorHandler.getColumnNumber() + ")";
            FacesMessage facesMessage = new FacesMessage(message);
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
}
