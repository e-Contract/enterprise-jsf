/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.taginfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jboss.vfs.VirtualFile;
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
    private String description;
    private String version;

    public String getNamespace() {
        return this.namespace;
    }

    public String getDescription() {
        return this.description;
    }

    public String getVersion() {
        return this.version;
    }

    public List<TagInfo> getTags() {
        List<TagInfo> tags = new LinkedList<>();
        String library = (String) getAttributes().get("library");
        if (null == library) {
            return tags;
        }
        Document taglibDocument = getTaglibDocument(library);
        if (null == taglibDocument) {
            return tags;
        }
        this.version = taglibDocument.getDocumentElement().getAttribute("version");
        if ("2.0".equals(this.version)) {
            this.version = "2.1";
        }
        NodeList descriptionNodeList = taglibDocument.getElementsByTagNameNS("*", "description");
        for (int descriptionIdx = 0; descriptionIdx < descriptionNodeList.getLength(); descriptionIdx++) {
            Element descriptionElement = (Element) descriptionNodeList.item(descriptionIdx);
            if (descriptionElement.getParentNode().equals(taglibDocument.getDocumentElement())) {
                this.description = descriptionElement.getTextContent();
                break;
            }
        }
        this.namespace = taglibDocument.getElementsByTagNameNS("*", "namespace").item(0).getTextContent();
        String tagName = (String) getAttributes().get("tag");
        NodeList tagNodeList = taglibDocument.getElementsByTagNameNS("*", "tag");
        LOGGER.debug("number of tags: {}", tagNodeList.getLength());
        for (int tagIdx = 0; tagIdx < tagNodeList.getLength(); tagIdx++) {
            Element tagElement = (Element) tagNodeList.item(tagIdx);
            String thisTagName = tagElement.getElementsByTagNameNS("*", "tag-name").item(0).getTextContent();
            if (null != tagName && !tagName.equals(thisTagName)) {
                continue;
            }
            String tagDescription = null;
            NodeList tagDescriptionNodeList = tagElement.getElementsByTagNameNS("*", "description");
            for (int tagDescriptionIdx = 0; tagDescriptionIdx < tagDescriptionNodeList.getLength(); tagDescriptionIdx++) {
                Element tagDescriptionElement = (Element) tagDescriptionNodeList.item(tagDescriptionIdx);
                if (tagDescriptionElement.getParentNode().equals(tagElement)) {
                    tagDescription = tagDescriptionElement.getTextContent();
                    break;
                }
            }
            TagInfo tagInfo = new TagInfo(thisTagName, tagDescription);
            tags.add(tagInfo);
            NodeList componentTypeNodeList = tagElement.getElementsByTagNameNS("*", "component-type");
            if (componentTypeNodeList.getLength() > 0) {
                String componentType = componentTypeNodeList.item(0).getTextContent();
                FacesContext facesContext = getFacesContext();
                Application application = facesContext.getApplication();
                UIComponent component = application.createComponent(componentType);
                if (component instanceof ClientBehaviorHolder) {
                    ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) component;
                    String defaultEventName = clientBehaviorHolder.getDefaultEventName();
                    tagInfo.setClientBehaviorDefaultEventName(defaultEventName);
                    tagInfo.getClientBehaviorEventNames().addAll(clientBehaviorHolder.getEventNames());
                }
            }
            NodeList attributeNodeList = tagElement.getElementsByTagNameNS("*", "attribute");
            for (int attributeIdx = 0; attributeIdx < attributeNodeList.getLength(); attributeIdx++) {
                Element attributeElement = (Element) attributeNodeList.item(attributeIdx);
                String attributeName = attributeElement.getElementsByTagNameNS("*", "name").item(0).getTextContent();
                String attributeType = null;
                NodeList typeNodeList = attributeElement.getElementsByTagNameNS("*", "type");
                if (typeNodeList.getLength() > 0) {
                    attributeType = typeNodeList.item(0).getTextContent();
                } else {
                    NodeList methodSignatureNodeList = attributeElement.getElementsByTagNameNS("*", "method-signature");
                    if (methodSignatureNodeList.getLength() > 0) {
                        attributeType = methodSignatureNodeList.item(0).getTextContent();
                    }
                }
                String attributeDescription;
                NodeList attributeDescriptionNodeList = attributeElement.getElementsByTagNameNS("*", "description");
                if (attributeDescriptionNodeList.getLength() > 0) {
                    attributeDescription = attributeDescriptionNodeList.item(0).getTextContent();
                } else {
                    attributeDescription = null;
                }
                boolean attributeRequired;
                NodeList requiredNodeList = attributeElement.getElementsByTagNameNS("*", "required");
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
        if (!tags.isEmpty() && null != tagName) {
            return tags;
        }
        tags.sort((tag1, tag2) -> tag1.getTagName().compareTo(tag2.getTagName()));
        NodeList compositeLibraryNameNodeList = taglibDocument.getElementsByTagNameNS("*", "composite-library-name");
        if (compositeLibraryNameNodeList.getLength() == 0) {
            return tags;
        }
        String compositeLibraryName = compositeLibraryNameNodeList.item(0).getTextContent();
        if (null != tagName) {
            TagInfo tag = getCompositeTagInfo(compositeLibraryName, tagName);
            if (null != tag) {
                tags.add(tag);
            }
            return tags;
        }
        URL compositeURL = TagInfoComponent.class.getResource("/META-INF/resources/" + compositeLibraryName + "/");
        LOGGER.debug("composite URL: {}", compositeURL);
        if (null == compositeURL) {
            return tags;
        }
        List<String> compositeTags;
        try {
            URI uri = compositeURL.toURI();
            if (uri.getScheme().equals("jar")) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    Path compositePath = fileSystem.getPath("/META-INF/resources/" + compositeLibraryName + "/");
                    compositeTags = Files.list(compositePath)
                            .filter(path -> path.getFileName().toString().endsWith(".xhtml"))
                            .map(path -> path.getFileName().toString())
                            .collect(Collectors.toList());
                }
            } else if (uri.getScheme().equals("vfs")) {
                // WildFly/JBoss specific
                URLConnection urlConnection = compositeURL.openConnection();
                VirtualFile virtualFile = (VirtualFile) urlConnection.getContent();
                compositeTags = new LinkedList<>();
                for (VirtualFile childVirtualFile : virtualFile.getChildren()) {
                    String childName = childVirtualFile.getName();
                    LOGGER.debug("child virtual file: {}", childName);
                    if (childName.endsWith(".xhtml")) {
                        compositeTags.add(childName);
                    }
                }
            } else {
                Path compositePath = Paths.get(uri);
                compositeTags = Files.list(compositePath)
                        .filter(path -> path.getFileName().toString().endsWith(".xhtml"))
                        .map(path -> path.getFileName().toString())
                        .collect(Collectors.toList());
            }
        } catch (URISyntaxException | IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return tags;
        }
        LOGGER.debug("composite tags: {}", compositeTags);
        for (String compositeTag : compositeTags) {
            String compositeTagName = compositeTag.substring(0, compositeTag.indexOf(".xhtml"));
            TagInfo tag = getCompositeTagInfo(compositeLibraryName, compositeTagName);
            if (null != tag) {
                tags.add(tag);
            }
        }
        tags.sort((tag1, tag2) -> tag1.getTagName().compareTo(tag2.getTagName()));
        return tags;
    }

    private TagInfo getCompositeTagInfo(String compositeLibraryName, String tagName) {
        Document compositeDocument = getCompositeTagDocument(compositeLibraryName, tagName);
        if (null == compositeDocument) {
            return null;
        }
        Element interfaceElement = (Element) compositeDocument.getElementsByTagNameNS("*", "interface").item(0);
        if (null == interfaceElement) {
            LOGGER.warn("missing interface element");
            return null;
        }
        String tagDescription = interfaceElement.getAttribute("shortDescription");
        TagInfo tagInfo = new TagInfo(tagName, tagDescription);
        String componentType = interfaceElement.getAttribute("componentType");
        if (null != componentType) {
            FacesContext facesContext = getFacesContext();
            Application application = facesContext.getApplication();
            UIComponent component = application.createComponent(componentType);
            if (component instanceof ClientBehaviorHolder) {
                ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) component;
                String defaultEventName = clientBehaviorHolder.getDefaultEventName();
                tagInfo.setClientBehaviorDefaultEventName(defaultEventName);
                tagInfo.getClientBehaviorEventNames().addAll(clientBehaviorHolder.getEventNames());
            }
        }
        NodeList attributeNodeList = interfaceElement.getElementsByTagNameNS("*", "attribute");
        for (int attributeIdx = 0; attributeIdx < attributeNodeList.getLength(); attributeIdx++) {
            Element attributeElement = (Element) attributeNodeList.item(attributeIdx);
            String attributeName = attributeElement.getAttribute("name");
            String attributeType = attributeElement.getAttribute("type");
            String attributeDescription = attributeElement.getAttribute("shortDescription");
            boolean attributeRequired;
            if (null != attributeElement.getAttribute("required")) {
                attributeRequired = Boolean.parseBoolean(attributeElement.getAttribute("required"));
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
        return tagInfo;
    }

    private Document getCompositeTagDocument(String compositeLibraryName, String tagName) {
        InputStream inputStream = TagInfoComponent.class.getResourceAsStream("/META-INF/resources/" + compositeLibraryName + "/" + tagName + ".xhtml");
        if (null == inputStream) {
            LOGGER.warn("composite not found: {}", tagName);
            return null;
        }
        try {
            return loadDocument(inputStream);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOGGER.error("error loading composite document: " + ex.getMessage(), ex);
            return null;
        }
    }

    private Document getTaglibDocument(String library) {
        InputStream taglibInputStream = TagInfoComponent.class.getResourceAsStream("/META-INF/" + library + ".taglib.xml");
        if (null == taglibInputStream) {
            LOGGER.warn("taglib not found: {}", library);
            return null;
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
