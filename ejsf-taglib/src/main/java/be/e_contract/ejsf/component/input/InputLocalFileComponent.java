/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.input;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.application.ProjectStage;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(InputLocalFileComponent.COMPONENT_TYPE)
public class InputLocalFileComponent extends UIInput implements NamingContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputLocalFileComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.inputLocalFileComponent";

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    private UIComponent browseContent;

    private UIInput input;

    public void setBrowseContent(UIComponent browsePanel) {
        this.browseContent = browsePanel;
    }

    public UIComponent getBrowseContent() {
        return this.browseContent;
    }

    public void setInput(UIInput input) {
        this.input = input;
    }

    public UIInput getInput() {
        return this.input;
    }

    public List<File> getFiles() {
        List<File> result = new LinkedList<>();
        if (!isAuthorized()) {
            return result;
        }
        String currentDirectory = getCurrentDirectory();
        File currentDirectoryFile = new File(currentDirectory);
        FileFilter fileFilter;
        String fileFilterAttr = (String) getAttributes().get("fileFilter");
        if (null != fileFilterAttr) {
            fileFilter = (File file) -> {
                if (file.isDirectory()) {
                    return true;
                }
                LOGGER.debug("file name: {}", file.getName());
                return file.getName().matches(fileFilterAttr);
            };
        } else {
            fileFilter = null;
        }
        File[] files = currentDirectoryFile.listFiles(fileFilter);
        if (null == files) {
            return result;
        }
        Arrays.sort(files, (File f1, File f2) -> f1.getName().compareTo(f2.getName()));
        for (File file : files) {
            result.add(file);
        }
        return result;
    }

    public String getCurrentDirectory() {
        if (!isAuthorized()) {
            return "Not authorized.";
        }
        String selectedDirectory = (String) getAttributes().get("selectedDirectory");
        if (null != selectedDirectory) {
            LOGGER.debug("current directory is a selected directory: {}", selectedDirectory);
            return selectedDirectory;
        }
        String currentDirectory = getOriginalDirectory();
        return currentDirectory;
    }

    private String getOriginalDirectory() {
        String userHome = System.getProperty("user.home");
        String directory = (String) getAttributes().get("directory");
        String originalDirectory = userHome + "/" + directory + "/";
        return originalDirectory;
    }

    public boolean isAuthorized() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        ProjectStage projectStage = application.getProjectStage();
        if (projectStage != ProjectStage.Production) {
            return true;
        }
        String requiredRole = (String) getAttributes().get("requiredRole");
        if (null == requiredRole) {
            return true;
        }
        ExternalContext externalContext = facesContext.getExternalContext();
        return externalContext.isUserInRole(requiredRole);
    }

    public void selectDirectory(String directoryName) {
        LOGGER.debug("select directory: {}", directoryName);
        if (!isAuthorized()) {
            return;
        }
        String currentDirectory = getCurrentDirectory();
        File currentDirectoryFile = new File(currentDirectory);
        File[] files = currentDirectoryFile.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }
            if (file.getName().equals(directoryName)) {
                String directory = currentDirectory + directoryName + "/";
                LOGGER.debug("selected directory: {}", directory);
                getAttributes().put("selectedDirectory", directory);
                PrimeFaces primeFaces = PrimeFaces.current();
                PrimeFaces.Ajax ajax = primeFaces.ajax();
                ajax.update(this.browseContent);
            }
        }
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        String value = (String) getSubmittedValue();
        if (null == value) {
            value = (String) getValue();
        }
        LOGGER.debug("encodeBegin: {}", value);
        this.input.setValue(value);
        if (!isValid()) {
            this.input.setValid(false);
        }
        super.encodeBegin(context);
    }

    @Override
    public void processDecodes(FacesContext context) {
        super.processDecodes(context);
        String value = (String) this.input.getSubmittedValue();
        LOGGER.debug("processDecodes: {}", value);
        setSubmittedValue(value);
    }

    public void selectFile(String fileName) {
        LOGGER.debug("select file: {}", fileName);
        if (!isAuthorized()) {
            return;
        }
        String currentDirectory = getCurrentDirectory();
        File currentDirectoryFile = new File(currentDirectory);
        File[] files = currentDirectoryFile.listFiles();
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            if (file.getName().equals(fileName)) {
                String value = currentDirectory + fileName;
                LOGGER.debug("value: {}", value);
                this.input.setSubmittedValue(value);
                PrimeFaces primeFaces = PrimeFaces.current();
                PrimeFaces.Ajax ajax = primeFaces.ajax();
                ajax.update(this);
            }
        }
    }

    public void selectParentDirectory() {
        LOGGER.debug("select parent directory");
        if (!isAuthorized()) {
            return;
        }
        String selectedDirectory = getCurrentDirectory();
        LOGGER.debug("current directory: {}", selectedDirectory);
        selectedDirectory = selectedDirectory.substring(0, selectedDirectory.lastIndexOf("/"));
        selectedDirectory = selectedDirectory.substring(0, selectedDirectory.lastIndexOf("/")) + "/";
        LOGGER.debug("new current directory: {}", selectedDirectory);
        String originalDirectory = getOriginalDirectory();
        if (!selectedDirectory.startsWith(originalDirectory)) {
            return;
        }
        getAttributes().put("selectedDirectory", selectedDirectory);
        PrimeFaces primeFaces = PrimeFaces.current();
        PrimeFaces.Ajax ajax = primeFaces.ajax();
        ajax.update(this.browseContent);
    }

    public boolean isRenderParentDirectory() {
        String originalDirectory = getOriginalDirectory();
        String selectedDirectory = getCurrentDirectory();
        return (!originalDirectory.equals(selectedDirectory));
    }
}
