/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.taginfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
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

    public enum PropertyKeys {
        actualNamespace,
        description,
        version,
        tags,
        functions
    }

    public String getActualNamespace() {
        return (String) getStateHelper().get(PropertyKeys.actualNamespace);
    }

    public String getDescription() {
        return (String) getStateHelper().get(PropertyKeys.description);
    }

    public String getVersion() {
        return (String) getStateHelper().get(PropertyKeys.version);
    }

    private void setActualNamespace(String namespace) {
        getStateHelper().put(PropertyKeys.actualNamespace, namespace);
    }

    private void setDescription(String description) {
        getStateHelper().put(PropertyKeys.description, description);
    }

    private void setVersion(String version) {
        getStateHelper().put(PropertyKeys.version, version);
    }

    public List<FunctionInfo> getFunctions() {
        getTags(); // parse
        List<FunctionInfo> functions = (List<FunctionInfo>) getStateHelper().get(PropertyKeys.functions);
        return functions;
    }

    public List<TagInfo> getTags() {
        LOGGER.debug("getTags");
        List<TagInfo> tags = (List<TagInfo>) getStateHelper().get(PropertyKeys.tags);
        if (null != tags) {
            LOGGER.debug("using cached tags");
            return tags;
        }
        tags = new LinkedList<>();
        getStateHelper().put(PropertyKeys.tags, tags);
        List<FunctionInfo> functions = new LinkedList<>();
        getStateHelper().put(PropertyKeys.functions, functions);
        String library = (String) getAttributes().get("library");
        if (null == library) {
            String namespace = (String) getAttributes().get("namespace");
            library = findLibraryForNamespace(namespace);
            if (null == library) {
                return tags;
            }
        }
        Document taglibDocument = getTaglibDocument(library);
        if (null == taglibDocument) {
            return tags;
        }
        String version = taglibDocument.getDocumentElement().getAttribute("version");
        if ("2.0".equals(version)) {
            version = "2.1";
        }
        setVersion(version);
        NodeList descriptionNodeList = taglibDocument.getElementsByTagNameNS("*", "description");
        for (int descriptionIdx = 0; descriptionIdx < descriptionNodeList.getLength(); descriptionIdx++) {
            Element descriptionElement = (Element) descriptionNodeList.item(descriptionIdx);
            if (descriptionElement.getParentNode().equals(taglibDocument.getDocumentElement())) {
                String description = descriptionElement.getTextContent();
                setDescription(description);
                break;
            }
        }
        String namespace = taglibDocument.getElementsByTagNameNS("*", "namespace").item(0).getTextContent();
        setActualNamespace(namespace);
        String tagName = (String) getAttributes().get("tag");
        NodeList tagNodeList = taglibDocument.getElementsByTagNameNS("*", "tag");
        LOGGER.trace("number of tags: {}", tagNodeList.getLength());
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
                tagInfo.setComponentType(componentType);
                FacesContext facesContext = getFacesContext();
                Application application = facesContext.getApplication();
                UIComponent component = application.createComponent(componentType);
                String componentClass = component.getClass().getName();
                tagInfo.setComponentClass(componentClass);
                if (component instanceof ClientBehaviorHolder) {
                    ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) component;
                    String defaultEventName = clientBehaviorHolder.getDefaultEventName();
                    tagInfo.setClientBehaviorDefaultEventName(defaultEventName);
                    Collection<String> eventNames = clientBehaviorHolder.getEventNames();
                    if (null != eventNames) {
                        tagInfo.getClientBehaviorEventNames().addAll(eventNames);
                    } else {
                        LOGGER.warn("component {} returns null for getEventNames()", componentType);
                    }
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
        if (null == tagName) {
            NodeList functionNodeList = taglibDocument.getElementsByTagNameNS("*", "function");
            for (int functionIdx = 0; functionIdx < functionNodeList.getLength(); functionIdx++) {
                Element functionElement = (Element) functionNodeList.item(functionIdx);
                String functionName = functionElement.getElementsByTagNameNS("*", "function-name").item(0).getTextContent();
                String functionClass = functionElement.getElementsByTagNameNS("*", "function-class").item(0).getTextContent();
                String functionSignature = functionElement.getElementsByTagNameNS("*", "function-signature").item(0).getTextContent();
                String functionDescription;
                NodeList functionDescriptionNodeList = functionElement.getElementsByTagNameNS("*", "description");
                if (functionDescriptionNodeList.getLength() > 0) {
                    functionDescription = functionDescriptionNodeList.item(0).getTextContent();
                } else {
                    functionDescription = null;
                }
                FunctionInfo functionInfo = new FunctionInfo(functionName, functionClass, functionSignature, functionDescription);
                functions.add(functionInfo);
            }
        }
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
            } else if (uri.getScheme().equals("wsjar")) {
                compositeTags = new LinkedList<>();
                LOGGER.warn("TODO: implement me");
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
        if (!UIInput.isEmpty(componentType)) {
            LOGGER.debug("componentType: {}", componentType);
            tagInfo.setComponentType(componentType);
            FacesContext facesContext = getFacesContext();
            Application application = facesContext.getApplication();
            UIComponent component;
            try {
                component = application.createComponent(componentType);
            } catch (FacesException ex) {
                LOGGER.error("error creating component: " + ex.getMessage(), ex);
                return tagInfo;
            }
            String componentClass = component.getClass().getName();
            tagInfo.setComponentClass(componentClass);
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
        LOGGER.debug("composite {} tag {}", compositeLibraryName, tagName);
        InputStream inputStream = TagInfoComponent.class.getResourceAsStream("/META-INF/resources/" + compositeLibraryName + "/" + tagName + ".xhtml");
        if (null == inputStream) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            ServletContext servletContext = (ServletContext) externalContext.getContext();
            inputStream = servletContext.getResourceAsStream("/resources/" + compositeLibraryName + "/" + tagName + ".xhtml");
            if (null == inputStream) {
                LOGGER.warn("composite not found: {}", tagName);
                return null;
            }
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

    private static final Map<String, String> NAMESPACE_TO_LIBRARY_MAP = new HashMap<>();

    private String findLibraryForNamespace(String namespace) {
        if (!NAMESPACE_TO_LIBRARY_MAP.isEmpty()) {
            return NAMESPACE_TO_LIBRARY_MAP.get(namespace);
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources("META-INF");
        } catch (IOException ex) {
            LOGGER.error("resources error: " + ex.getMessage(), ex);
            return null;
        }
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            URLConnection urlConnection;
            try {
                urlConnection = resource.openConnection();
            } catch (IOException ex) {
                LOGGER.error("URL connection error: " + ex.getMessage(), ex);
                continue;
            }
            if (urlConnection instanceof JarURLConnection) {
                JarURLConnection jarUrlConnection = (JarURLConnection) urlConnection;
                JarFile jarFile;
                try {
                    jarFile = jarUrlConnection.getJarFile();
                } catch (IOException ex) {
                    LOGGER.error("get jar file error: " + ex.getMessage(), ex);
                    continue;
                }
                Enumeration<JarEntry> jarEntryEnum = jarFile.entries();
                while (jarEntryEnum.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnum.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (!jarEntryName.startsWith("META-INF/")) {
                        continue;
                    }
                    if (!jarEntryName.endsWith(".taglib.xml")) {
                        continue;
                    }
                    String library = jarEntryName.substring("/META-INF".length());
                    library = library.substring(0, library.indexOf(".taglib.xml"));
                    Document taglibDocument;
                    try {
                        taglibDocument = loadDocument(jarFile.getInputStream(jarEntry));
                    } catch (ParserConfigurationException | SAXException | IOException ex) {
                        LOGGER.error("error loading taglib XML document: " + ex.getMessage(), ex);
                        continue;
                    }
                    String taglibNamespace = taglibDocument.getElementsByTagNameNS("*", "namespace").item(0).getTextContent();
                    LOGGER.debug("namespace {} - library: {}", taglibNamespace, library);
                    NAMESPACE_TO_LIBRARY_MAP.put(taglibNamespace, library);
                }
            } else if (resource.getProtocol().equals("vfs")) {
                // WildFly/JBoss
                VirtualFile virtualFile;
                try {
                    virtualFile = (VirtualFile) urlConnection.getContent();
                } catch (IOException ex) {
                    LOGGER.error("VFS error: " + ex.getMessage(), ex);
                    continue;
                }
                for (VirtualFile childVirtualFile : virtualFile.getChildren()) {
                    String childVirtualFileName = childVirtualFile.getName();
                    LOGGER.debug("child: {}", childVirtualFileName);
                    if (childVirtualFileName.endsWith(".taglib.xml")) {
                        String library = childVirtualFileName.substring(0, childVirtualFileName.indexOf(".taglib.xml"));
                        Document taglibDocument;
                        try {
                            taglibDocument = loadDocument(childVirtualFile.openStream());
                        } catch (ParserConfigurationException | SAXException | IOException ex) {
                            LOGGER.error("error loading taglib XML document: " + ex.getMessage(), ex);
                            continue;
                        }
                        String taglibNamespace = taglibDocument.getElementsByTagNameNS("*", "namespace").item(0).getTextContent();
                        LOGGER.debug("namespace {} - library: {}", taglibNamespace, library);
                        NAMESPACE_TO_LIBRARY_MAP.put(taglibNamespace, library);
                    }
                }
            } else {
                LOGGER.debug("resource: {}", resource);
                LOGGER.warn("TODO: implement me");
            }
        }
        return NAMESPACE_TO_LIBRARY_MAP.get(namespace);
    }
}
