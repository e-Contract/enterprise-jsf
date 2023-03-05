/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.taginfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@FacesComponent(TagInfoComponent.COMPONENT_TYPE)
public class TagInfoComponent extends UIComponentBase implements NamingContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagInfoComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.tagInfo";

    public TagInfoComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    private String namespace;

    public String getNamespace() {
        return this.namespace;
    }

    public List<TagInfo> getTags() {
        List<TagInfo> tags = new LinkedList<>();
        String library = (String) getAttributes().get("library");
        Document taglibDocument;
        try {
            taglibDocument = getTaglibDocument(library);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOGGER.error("error loading taglib document: " + ex.getMessage(), ex);
            return tags;
        }
        if (null == taglibDocument) {
            return tags;
        }
        this.namespace = taglibDocument.getElementsByTagName("namespace").item(0).getTextContent();
        String tagName = (String) getAttributes().get("tag");
        NodeList tagNodeList = taglibDocument.getElementsByTagName("tag");
        LOGGER.debug("number of tags: {}", tagNodeList.getLength());
        for (int tagIdx = 0; tagIdx < tagNodeList.getLength(); tagIdx++) {
            Element tagElement = (Element) tagNodeList.item(tagIdx);
            String thisTagName = tagElement.getElementsByTagName("tag-name").item(0).getTextContent();
            if (null != tagName && !tagName.equals(thisTagName)) {
                continue;
            }
            String tagDescription = null;
            NodeList tagDescriptionNodeList = tagElement.getElementsByTagName("description");
            for (int tagDescriptionIdx = 0; tagDescriptionIdx < tagDescriptionNodeList.getLength(); tagDescriptionIdx++) {
                Element tagDescriptionElement = (Element) tagDescriptionNodeList.item(tagDescriptionIdx);
                if (tagDescriptionElement.getParentNode().equals(tagElement)) {
                    tagDescription = tagDescriptionElement.getTextContent();
                    break;
                }
            }
            TagInfo tagInfo = new TagInfo(tagName, tagDescription);
            tags.add(tagInfo);
            NodeList attributeNodeList = tagElement.getElementsByTagName("attribute");
            for (int attributeIdx = 0; attributeIdx < attributeNodeList.getLength(); attributeIdx++) {
                Element attributeElement = (Element) attributeNodeList.item(attributeIdx);
                String attributeName = attributeElement.getElementsByTagName("name").item(0).getTextContent();
                String attributeType = null;
                NodeList typeNodeList = attributeElement.getElementsByTagName("type");
                if (typeNodeList.getLength() > 0) {
                    attributeType = typeNodeList.item(0).getTextContent();
                } else {
                    NodeList methodSignatureNodeList = attributeElement.getElementsByTagName("method-signature");
                    if (methodSignatureNodeList.getLength() > 0) {
                        attributeType = methodSignatureNodeList.item(0).getTextContent();
                    }
                }
                String attributeDescription;
                NodeList attributeDescriptionNodeList = attributeElement.getElementsByTagName("description");
                if (attributeDescriptionNodeList.getLength() > 0) {
                    attributeDescription = attributeDescriptionNodeList.item(0).getTextContent();
                } else {
                    attributeDescription = null;
                }
                boolean attributeRequired;
                NodeList requiredNodeList = attributeElement.getElementsByTagName("required");
                if (requiredNodeList.getLength() > 0) {
                    attributeRequired = Boolean.parseBoolean(requiredNodeList.item(0).getTextContent());
                } else {
                    attributeRequired = false;
                }
                TagAttribute tagAttribute = new TagAttribute(attributeName, attributeType, attributeDescription);
                List<TagAttribute> tagAttributes;
                if (attributeRequired) {
                    tagAttributes = tagInfo.getRequiredAttributes();
                } else {
                    tagAttributes = tagInfo.getOptionalAttributes();
                }
                tagAttributes.add(tagAttribute);
            }
        }
        return tags;
    }

    private Document getTaglibDocument(String library) throws ParserConfigurationException, SAXException, IOException {
        InputStream taglibInputStream = TagInfoComponent.class.getResourceAsStream("/META-INF/" + library + ".taglib.xml");
        if (null == taglibInputStream) {
            LOGGER.warn("taglib not found: {}", library);
            return null;
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(taglibInputStream);
        return document;
    }
}