/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.input.template;

import be.e_contract.ejsf.component.input.InputComponent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Function;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectItem;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.primefaces.component.checkbox.Checkbox;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.radiobutton.RadioButton;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@FacesComponent(InputTemplateComponent.COMPONENT_TYPE)
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class InputTemplateComponent extends UIInput implements NamingContainer, ComponentSystemEventListener {

    public static final String COMPONENT_TYPE = "ejsf.inputTemplateComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    private static final Logger LOGGER = LoggerFactory.getLogger(InputComponent.class);

    public InputTemplateComponent() {
        setRendererType(null);
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

    private int toComponents(Node parentNode, UIComponent parentComponent, int inputComponentIndex, Application application) {
        NodeList childNodes = parentNode.getChildNodes();
        for (int idx = 0; idx < childNodes.getLength(); idx++) {
            Node childNode = childNodes.item(idx);
            LOGGER.debug("child node type: {}", childNode.getNodeType());
            switch (childNode.getNodeType()) {
                case Node.TEXT_NODE:
                    UIOutput output = (UIOutput) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
                    output.setValue(childNode.getTextContent());
                    parentComponent.getChildren().add(output);
                    break;
                case Node.ELEMENT_NODE:
                    Element element = (Element) childNode;
                    String localName = element.getLocalName();
                    LOGGER.debug("element local name: {}", localName);
                    if ("xref".equals(localName)) {
                        UIOutput xref = (UIOutput) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
                        xref.setValue(element.getAttribute("id").toUpperCase());
                        parentComponent.getChildren().add(xref);
                    } else if ("assignment".equals(localName)) {
                        InputText inputComponent = (InputText) application.createComponent(InputText.COMPONENT_TYPE);
                        String inputComponentId = "input-" + Integer.toString(inputComponentIndex);
                        inputComponentIndex++;
                        inputComponent.setId(inputComponentId);
                        element.setAttribute("ejsf-input", inputComponentId);
                        parentComponent.getChildren().add(inputComponent);

                        NodeList itemNodes = element.getChildNodes();
                        for (int itemIdx = 0; itemIdx < itemNodes.getLength(); itemIdx++) {
                            Node itemNode = itemNodes.item(itemIdx);
                            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element itemElement = (Element) itemNode;
                                if ("assignmentitem".equals(itemElement.getLocalName())) {
                                    inputComponent.setPlaceholder(itemElement.getTextContent());
                                }
                            }
                        }
                    } else if ("list".equals(localName)) {
                        String type = element.getAttribute("type");
                        if ("itemized".equals(type)) {
                            HtmlComponent ul = (HtmlComponent) application.createComponent(HtmlComponent.COMPONENT_TYPE);
                            ul.setTag("ul");
                            parentComponent.getChildren().add(ul);
                            NodeList itemNodes = childNode.getChildNodes();
                            for (int itemIdx = 0; itemIdx < itemNodes.getLength(); itemIdx++) {
                                Node itemNode = itemNodes.item(itemIdx);
                                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element itemElement = (Element) itemNode;
                                    if ("item".equals(itemElement.getLocalName())) {
                                        HtmlComponent li = (HtmlComponent) application.createComponent(HtmlComponent.COMPONENT_TYPE);
                                        li.setTag("li");
                                        ul.getChildren().add(li);
                                        inputComponentIndex = toComponents(itemElement, li, inputComponentIndex, application);
                                    }
                                }
                            }
                        }
                    } else if ("selection".equals(localName)) {
                        String exclusive = element.getAttribute("exclusive");
                        if ("YES".equals(exclusive)) {
                            SelectOneRadio selectOneRadio = (SelectOneRadio) application.createComponent(SelectOneRadio.COMPONENT_TYPE);
                            String inputComponentId = "input-" + Integer.toString(inputComponentIndex);
                            inputComponentIndex++;
                            selectOneRadio.setId(inputComponentId);
                            selectOneRadio.setLayout("custom");
                            element.setAttribute("ejsf-input", inputComponentId);
                            parentComponent.getChildren().add(selectOneRadio);
                            HtmlComponent ul = (HtmlComponent) application.createComponent(HtmlComponent.COMPONENT_TYPE);
                            ul.setTag("ul");
                            ul.setStyle("list-style-type: none;");
                            parentComponent.getChildren().add(ul);
                            int itemIndex = 0;
                            NodeList itemNodes = childNode.getChildNodes();
                            for (int itemNodeIdx = 0; itemNodeIdx < itemNodes.getLength(); itemNodeIdx++) {
                                Node itemNode = itemNodes.item(itemNodeIdx);
                                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element itemElement = (Element) itemNode;
                                    if ("selectionitem".equals(itemElement.getLocalName())) {
                                        UISelectItem selectItem = (UISelectItem) application.createComponent(UISelectItem.COMPONENT_TYPE);
                                        selectOneRadio.getChildren().add(selectItem);
                                        selectItem.setItemLabel(itemElement.getTextContent());
                                        String itemValue = "item-" + itemIndex;
                                        itemElement.setAttribute("ejsf-input-item", itemValue);
                                        selectItem.setItemValue(itemValue);

                                        HtmlComponent li = (HtmlComponent) application.createComponent(HtmlComponent.COMPONENT_TYPE);
                                        li.setTag("li");
                                        ul.getChildren().add(li);
                                        RadioButton radioButton = (RadioButton) application.createComponent(RadioButton.COMPONENT_TYPE);
                                        radioButton.setItemIndex(itemIndex);
                                        radioButton.setFor(inputComponentId);
                                        radioButton.setId(inputComponentId + "-" + itemValue);
                                        itemIndex++;
                                        li.getChildren().add(radioButton);
                                        toComponents(itemElement, li, inputComponentIndex, application);
                                    }
                                }
                            }
                        } else {
                            // exclusive="NO"
                            SelectManyCheckbox selectManyCheckbox = (SelectManyCheckbox) application.createComponent(SelectManyCheckbox.COMPONENT_TYPE);
                            String inputComponentId = "input-" + Integer.toString(inputComponentIndex);
                            inputComponentIndex++;
                            selectManyCheckbox.setId(inputComponentId);
                            selectManyCheckbox.setLayout("custom");
                            element.setAttribute("ejsf-input", inputComponentId);
                            parentComponent.getChildren().add(selectManyCheckbox);
                            HtmlComponent ul = (HtmlComponent) application.createComponent(HtmlComponent.COMPONENT_TYPE);
                            ul.setTag("ul");
                            ul.setStyle("list-style-type: none;");
                            parentComponent.getChildren().add(ul);
                            NodeList itemNodes = childNode.getChildNodes();
                            int itemIndex = 0;
                            for (int itemNodeIdx = 0; itemNodeIdx < itemNodes.getLength(); itemNodeIdx++) {
                                Node itemNode = itemNodes.item(itemNodeIdx);
                                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element itemElement = (Element) itemNode;
                                    if ("selectionitem".equals(itemElement.getLocalName())) {
                                        UISelectItem selectItem = (UISelectItem) application.createComponent(UISelectItem.COMPONENT_TYPE);
                                        selectManyCheckbox.getChildren().add(selectItem);
                                        selectItem.setItemLabel(itemElement.getTextContent());
                                        String itemValue = "item-" + itemIndex;
                                        itemElement.setAttribute("ejsf-input-item", itemValue);
                                        selectItem.setItemValue(itemValue);

                                        HtmlComponent li = (HtmlComponent) application.createComponent(HtmlComponent.COMPONENT_TYPE);
                                        li.setTag("li");
                                        ul.getChildren().add(li);
                                        Checkbox checkbox = (Checkbox) application.createComponent(Checkbox.COMPONENT_TYPE);
                                        checkbox.setItemIndex(itemIndex);
                                        checkbox.setFor(inputComponentId);
                                        checkbox.setId(inputComponentId + "-" + itemValue);
                                        itemIndex++;
                                        li.getChildren().add(checkbox);
                                        toComponents(itemElement, li, inputComponentIndex, application);
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return inputComponentIndex;
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (event instanceof PostAddToViewEvent) {
            String input = (String) getValue();
            Document document = loadDocument(input);
            FacesContext facesContext = event.getFacesContext();
            Application application = facesContext.getApplication();
            int inputComponentIndex = 0;
            toComponents(document.getDocumentElement(), this, inputComponentIndex, application);
            setValue(toString(document));
        }
        super.processEvent(event);
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
        String input = (String) getValue();
        Document document = loadDocument(input);
        updateDocumentValue(getChildren(), (UIInput inputComponent) -> inputComponent.getSubmittedValue(), document);
        setValue(toString(document));
        updateRequired(getChildren(), document);
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
        Document document = loadDocument(input);

        updateDocumentValue(getChildren(), (UIInput inputComponent) -> inputComponent.getValue(), document);

        setValue(toString(document));
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
                if (value.getClass().isArray()) {
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

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);

        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("div", this);
        String clientId = getClientId(context);
        responseWriter.writeAttribute("id", clientId, "id");
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        super.encodeEnd(context);

        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.endElement("div");
    }
}
