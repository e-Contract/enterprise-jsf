/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.input.template;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Function;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.primefaces.component.api.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@FacesComponent(InputTemplateComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "primefaces", name = "components.css"),
    @ResourceDependency(library = "ejsf", name = "template/template.js"),
    @ResourceDependency(library = "ejsf", name = "template/template-widget.js")
})
public class InputTemplateComponent extends UIInput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.inputTemplateComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    private static final Logger LOGGER = LoggerFactory.getLogger(InputTemplateComponent.class);

    public InputTemplateComponent() {
        setRendererType(InputTemplateRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        result,
    }

    public void setResult(ValueExpression result) {
        getStateHelper().put(PropertyKeys.result, result);
    }

    public ValueExpression getResult() {
        return (ValueExpression) getStateHelper().get(PropertyKeys.result);
    }

    private void toResult(Node parentNode, StringBuilder stringBuilder) {
        NodeList childNodes = parentNode.getChildNodes();
        for (int idx = 0; idx < childNodes.getLength(); idx++) {
            Node childNode = childNodes.item(idx);
            switch (childNode.getNodeType()) {
                case Node.TEXT_NODE:
                    stringBuilder.append(childNode.getTextContent().trim());
                    break;
                case Node.ELEMENT_NODE:
                    Element element = (Element) childNode;
                    String localName = element.getLocalName();
                    LOGGER.debug("element local name: {}", localName);
                    if ("xref".equals(localName)) {
                        String value = element.getAttribute("id").toUpperCase();
                        stringBuilder.append(" ");
                        stringBuilder.append(value);
                        stringBuilder.append(" ");
                    } else if ("assignment".equals(localName)) {
                        String value = element.getAttribute("ejsf-input-value");
                        stringBuilder.append(" [");
                        stringBuilder.append(value);
                        stringBuilder.append("] ");
                    } else if ("list".equals(localName)) {
                        String type = element.getAttribute("type");
                        if ("itemized".equals(type)) {
                            stringBuilder.append("<ul>");
                            NodeList itemNodes = element.getChildNodes();
                            for (int itemIdx = 0; itemIdx < itemNodes.getLength(); itemIdx++) {
                                Node itemNode = itemNodes.item(itemIdx);
                                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element itemElement = (Element) itemNode;
                                    if ("item".equals(itemElement.getLocalName())) {
                                        stringBuilder.append("<li>");
                                        toResult(itemElement, stringBuilder);
                                        stringBuilder.append("</li>");
                                    }
                                }
                            }
                            stringBuilder.append("</ul>");
                        }
                    } else if ("selection".equals(localName)) {
                        String exclusive = element.getAttribute("exclusive");
                        if ("YES".equals(exclusive)) {
                            String itemValue = element.getAttribute("ejsf-input-value");
                            stringBuilder.append(" [<ul><li>");
                            NodeList itemNodes = element.getChildNodes();
                            for (int itemIdx = 0; itemIdx < itemNodes.getLength(); itemIdx++) {
                                Node itemNode = itemNodes.item(itemIdx);
                                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element itemElement = (Element) itemNode;
                                    if ("selectionitem".equals(itemElement.getLocalName())) {
                                        String thisItemValue = itemElement.getAttribute("ejsf-input-item");
                                        if (itemValue.equals(thisItemValue)) {
                                            toResult(itemElement, stringBuilder);
                                            break;
                                        }
                                    }
                                }
                            }
                            stringBuilder.append("</li></ul>] ");
                        } else {
                            // exclusive="NO"
                            String itemValues = element.getAttribute("ejsf-input-value");
                            stringBuilder.append(" [<ul>");
                            NodeList itemNodes = element.getChildNodes();
                            StringTokenizer stringTokenizer = new StringTokenizer(itemValues, ",");
                            while (stringTokenizer.hasMoreTokens()) {
                                String itemValue = stringTokenizer.nextToken();
                                stringBuilder.append("<li>");
                                for (int itemIdx = 0; itemIdx < itemNodes.getLength(); itemIdx++) {
                                    Node itemNode = itemNodes.item(itemIdx);
                                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element itemElement = (Element) itemNode;
                                        if ("selectionitem".equals(itemElement.getLocalName())) {
                                            String thisItemValue = itemElement.getAttribute("ejsf-input-item");
                                            if (itemValue.equals(thisItemValue)) {
                                                toResult(itemElement, stringBuilder);
                                                break;
                                            }
                                        }
                                    }
                                }
                                stringBuilder.append("</li>");
                            }
                            stringBuilder.append("</ul>] ");
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        LOGGER.debug("processValidators");
        super.processValidators(context);
    }

    private void updateRequired(List<UIComponent> components, Document document) {
        for (UIComponent component : components) {
            if (component instanceof UIInput) {
                UIInput inputComponent = (UIInput) component;
                String inputComponentId = inputComponent.getId();
                Element inputElement = findInputElement(inputComponentId, document);
                inputComponent.setRequired(isRequired(inputElement));
            }
            updateRequired(component.getChildren(), document);
        }
    }

    private boolean isRequired(Element inputElement) {
        Node node = inputElement.getParentNode();
        String itemValue = null;
        while (null != node) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String localName = element.getLocalName();
                if ("selectionitem".equals(localName)) {
                    if (null == itemValue) {
                        itemValue = element.getAttribute("ejsf-input-item");
                    }
                } else if ("selection".equals(localName)) {
                    StringTokenizer stringTokenizer = new StringTokenizer(element.getAttribute("ejsf-input-value"), ",");
                    while (stringTokenizer.hasMoreTokens()) {
                        String selectedValue = stringTokenizer.nextToken();
                        if (selectedValue.equals(itemValue)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
            node = node.getParentNode();
        }
        return true;
    }

    @Override
    public void updateModel(FacesContext context) {
        LOGGER.debug("updateModel");
        String input = (String) getValue();
        if (null == input) {
            return;
        }
        Document document = loadDocument(input);
        if (null == document) {
            return;
        }

        super.updateModel(context);

        StringBuilder stringBuilder = new StringBuilder();
        toResult(document.getDocumentElement(), stringBuilder);
        ValueExpression resultValueExpression = getResult();
        ELContext elContext = context.getELContext();
        resultValueExpression.setValue(elContext, stringBuilder.toString());
    }

    private Element findInputElement(String id, Node parent) {
        NodeList childNodes = parent.getChildNodes();
        for (int idx = 0; idx < childNodes.getLength(); idx++) {
            Node childNode = childNodes.item(idx);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element childElement = (Element) childNode;
            String childId = childElement.getAttribute("ejsf-input");
            if (null == childId) {
                continue;
            }
            if (id.equals(childId)) {
                return childElement;
            }
            Element inputElement = findInputElement(id, childElement);
            if (null != inputElement) {
                return inputElement;
            }
        }
        return null;
    }

    private void updateDocumentValue(List<UIComponent> components,
            Function<UIInput, Object> valueFunc,
            Document document) {
        for (UIComponent component : components) {
            if (component instanceof UIInput) {
                UIInput inputComponent = (UIInput) component;
                String inputComponentId = inputComponent.getId();
                Element inputElement = findInputElement(inputComponentId, document);
                Object value = valueFunc.apply(inputComponent);
                if (null == value) {
                    inputElement.setAttribute("ejsf-input-value", null);
                } else if (value.getClass().isArray()) {
                    String[] strValues = (String[]) value;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String strValue : strValues) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(strValue);
                        inputElement.setAttribute("ejsf-input-value", stringBuilder.toString());
                    }
                } else {
                    inputElement.setAttribute("ejsf-input-value", value.toString());
                }
            }
            updateDocumentValue(component.getChildren(), valueFunc, document);
        }
    }

    private Document loadDocument(String documentText) {
        if (null == documentText) {
            return null;
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
            return null;
        }
        try {
            return documentBuilder.parse(new InputSource(new StringReader(documentText)));
        } catch (SAXException | IOException ex) {
            return null;
        }
    }

    private String toString(Document document) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        try {
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        } catch (TransformerException ex) {
            return null;
        }
        return stringWriter.toString();
    }
}
