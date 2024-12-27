/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.highlight;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.IOUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@FacesRenderer(componentFamily = HighlightComponent.COMPONENT_FAMILY,
        rendererType = HighlightRenderer.RENDERER_TYPE)
public class HighlightRenderer extends CoreRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HighlightRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.highlightRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        HighlightComponent highlightComponent = (HighlightComponent) component;
        String language = highlightComponent.getLanguage();
        String value = (String) highlightComponent.getValue();
        if (null == value) {
            String _for = highlightComponent.getFor();
            if (null != _for) {
                Document document;
                try {
                    document = getDocument(facesContext);
                } catch (ParserConfigurationException | SAXException ex) {
                    LOGGER.warn("XML parser error: {}", ex.getMessage());
                    return;
                }
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList nodeList;
                try {
                    nodeList = (NodeList) xPath.evaluate("//*[@id=\"" + _for + "\"]", document.getDocumentElement(), XPathConstants.NODESET);
                } catch (XPathExpressionException ex) {
                    LOGGER.warn("XPath error: {}", ex.getMessage());
                    return;
                }
                if (nodeList.getLength() == 0) {
                    LOGGER.warn("element not found: {}", _for);
                    return;
                }
                Element element = (Element) nodeList.item(0);
                try {
                    value = toString(element);
                } catch (TransformerException ex) {
                    LOGGER.warn("XML parser error: {}", ex.getMessage());
                    return;
                }
                language = "html";
            }
            String resource = highlightComponent.getResource();
            if (null != resource) {
                ExternalContext externalContext = facesContext.getExternalContext();
                InputStream resourceInputStream = externalContext.getResourceAsStream(resource);
                if (null == resourceInputStream) {
                    resourceInputStream = HighlightRenderer.class.getResourceAsStream(resource);
                }
                if (null == resourceInputStream) {
                    LOGGER.warn("resource not found:{}", resource);
                    return;
                }
                value = IOUtils.toString(resourceInputStream, "UTF-8");
            }
        }

        String clientId = highlightComponent.getClientId();
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.startElement("pre", component);
        responseWriter.writeAttribute("class", "ejsf-highlight", null);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.startElement("code", null);
        if (null != language) {
            responseWriter.writeAttribute("class", "language-" + language, "language");
        }
        if (null != value) {
            responseWriter.writeText(value, null);
        }
        responseWriter.endElement("code");
        responseWriter.endElement("pre");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFHighlight", highlightComponent);
        widgetBuilder.finish();
    }

    private Document getDocument(FacesContext facesContext) throws ParserConfigurationException, SAXException, IOException {
        UIViewRoot viewRoot = facesContext.getViewRoot();
        String viewId = viewRoot.getViewId();
        ExternalContext externalContext = facesContext.getExternalContext();
        InputStream xhtmlInputStream = externalContext.getResourceAsStream(viewId);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xhtmlInputStream);
        return document;
    }

    private String toString(Element element) throws TransformerConfigurationException, TransformerException {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter buffer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(element),
                new StreamResult(buffer));
        String str = buffer.toString();
        return str;
    }
}
