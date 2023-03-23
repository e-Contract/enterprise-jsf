/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.primefaces.component.api.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@FacesComponent(TestComponentComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "test-component.js")
})
public class TestComponentComponent extends UIComponentBase implements Widget, SystemEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestComponentComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.testComponentComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public TestComponentComponent() {
        LOGGER.debug("constructor");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = facesContext.getViewRoot();
        viewRoot.subscribeToViewEvent(PostAddToViewEvent.class, this);
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return (source instanceof UIViewRoot);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        library,
        tag,
        tests,
        currentTestIndex
    }

    public String getLibrary() {
        return (String) getStateHelper().eval(PropertyKeys.library);
    }

    public void setLibrary(String library) {
        getStateHelper().put(PropertyKeys.library, library);
    }

    public String getTag() {
        return (String) getStateHelper().eval(PropertyKeys.tag);
    }

    public void setTag(String tag) {
        getStateHelper().put(PropertyKeys.tag, tag);
    }

    public List<ComponentTest> getTests() {
        return (List<ComponentTest>) getStateHelper().get(PropertyKeys.tests);
    }

    private List<ComponentTest> initTests() {
        List<ComponentTest> tests = new LinkedList<>();
        getStateHelper().put(PropertyKeys.tests, tests);
        String library = getLibrary();
        String tagName = getTag();
        Document taglibDocument = getTaglibDocument(library);
        if (null == taglibDocument) {
            return tests;
        }
        NodeList tagNodeList = taglibDocument.getElementsByTagNameNS("*", "tag");
        for (int tagIdx = 0; tagIdx < tagNodeList.getLength(); tagIdx++) {
            Element tagElement = (Element) tagNodeList.item(tagIdx);
            String thisTagName = tagElement.getElementsByTagNameNS("*", "tag-name").item(0).getTextContent();
            if (null != tagName && !tagName.equals(thisTagName)) {
                continue;
            }
            NodeList attributeNodeList = tagElement.getElementsByTagNameNS("*", "attribute");
            for (int attributeIdx = 0; attributeIdx < attributeNodeList.getLength(); attributeIdx++) {
                Element attributeElement = (Element) attributeNodeList.item(attributeIdx);
                String attributeName = attributeElement.getElementsByTagNameNS("*", "name").item(0).getTextContent();
                NodeList typeNodeList = attributeElement.getElementsByTagNameNS("*", "type");
                if (typeNodeList.getLength() == 0) {
                    continue;
                }
                String attributeType = typeNodeList.item(0).getTextContent();
                if (!"java.lang.String".equals(attributeType)) {
                    continue;
                }
                XSSComponentTest xssComponentTest = new XSSComponentTest(attributeName);
                LOGGER.debug("adding test: {}", xssComponentTest.getName());
                tests.add(xssComponentTest);
            }
        }
        return tests;
    }

    public ComponentTest getNextTest() {
        List<ComponentTest> tests = getTests();
        if (null == tests) {
            tests = initTests();
        }
        Integer currentTestIndex = (Integer) getStateHelper().get(PropertyKeys.currentTestIndex);
        if (null == currentTestIndex) {
            currentTestIndex = 0;
        }
        LOGGER.debug("current test index: {}", currentTestIndex);
        if (currentTestIndex >= tests.size()) {
            return null;
        }
        ComponentTest componentTest = tests.get(currentTestIndex);
        getStateHelper().put(PropertyKeys.currentTestIndex, ++currentTestIndex);
        return componentTest;
    }

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        if (!(event instanceof PostAddToViewEvent)) {
            return;
        }
        FacesContext facesContext = event.getFacesContext();
        LOGGER.debug("PostAddToViewEvent");
        String library = getLibrary();
        String tagName = getTag();
        LOGGER.debug("library: {}", library);
        LOGGER.debug("tag: {}", tagName);
        Document taglibDocument = getTaglibDocument(library);
        if (null == taglibDocument) {
            LOGGER.warn("tag library not found: {}", library);
            return;
        }
        NodeList tagNodeList = taglibDocument.getElementsByTagNameNS("*", "tag");
        for (int tagIdx = 0; tagIdx < tagNodeList.getLength(); tagIdx++) {
            Element tagElement = (Element) tagNodeList.item(tagIdx);
            String thisTagName = tagElement.getElementsByTagNameNS("*", "tag-name").item(0).getTextContent();
            if (null != tagName && !tagName.equals(thisTagName)) {
                continue;
            }
            NodeList componentTypeNodeList = tagElement.getElementsByTagNameNS("*", "component-type");
            if (componentTypeNodeList.getLength() == 0) {
                return;
            }
            String componentType = componentTypeNodeList.item(0).getTextContent();
            Application application = facesContext.getApplication();
            UIComponent component = application.createComponent(componentType);
            component.setId("test");
            getChildren().add(component);
            LOGGER.debug("added child component: {}", componentType);
        }
    }

    private Document getTaglibDocument(String library) {
        InputStream taglibInputStream = TestComponentComponent.class.getResourceAsStream("/META-INF/" + library + ".taglib.xml");
        if (null == taglibInputStream) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            ServletContext servletContext = (ServletContext) externalContext.getContext();
            taglibInputStream = servletContext.getResourceAsStream("/WEB-INF/" + library + ".taglib.xml");
            if (null == taglibInputStream) {
                LOGGER.warn("taglib not found: {}", library);
                return null;
            }
        }
        try {
            return loadDocument(taglibInputStream);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOGGER.error("error loading taglib document: " + ex.getMessage(), ex);
            return null;
        }
    }

    private Document loadDocument(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);
        return document;
    }
}
