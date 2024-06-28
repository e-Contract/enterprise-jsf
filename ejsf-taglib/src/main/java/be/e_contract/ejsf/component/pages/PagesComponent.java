/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.pages;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(PagesComponent.COMPONENT_TYPE)
public class PagesComponent extends UIComponentBase implements NamingContainer {

    public static final String COMPONENT_TYPE = "ejsf.pagesComponent";

    private static final String PAGES_MAP_ATTRIBUTE = PagesComponent.class.getName() + ".pagesMap";

    private static final String PAGES_ATTRIBUTE = PagesComponent.class.getName() + ".pages";

    private static final Logger LOGGER = LoggerFactory.getLogger(PagesComponent.class);

    private UIComponent pagesDataTables;

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    public void setPagesDataTable(UIComponent pagesDataTable) {
        this.pagesDataTables = pagesDataTable;
    }

    public UIComponent getPagesDataTable() {
        return this.pagesDataTables;
    }

    public List<Page> getPages() {
        getPagesMap();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, Object> applicationMap = externalContext.getApplicationMap();
        List<Page> pages = (List<Page>) applicationMap.get(PAGES_ATTRIBUTE);
        return pages;
    }

    private static Map<String, Page> getPagesMap() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, Object> applicationMap = externalContext.getApplicationMap();
        Map<String, Page> pagesMap = (Map<String, Page>) applicationMap.get(PAGES_MAP_ATTRIBUTE);
        if (null != pagesMap) {
            return pagesMap;
        }
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        String webAppRootPath = servletContext.getRealPath("/");
        LOGGER.debug("web app root path: {}", webAppRootPath);
        File webAppRootFile = new File(webAppRootPath);
        File[] files = webAppRootFile.listFiles();
        List<Page> pages = new LinkedList<>();
        addPages(true, files, pages);
        pages.sort((Page page1, Page page2) -> page1.getId().compareTo(page2.getId()));
        pagesMap = new LinkedHashMap<>();
        for (Page page : pages) {
            pagesMap.put(page.getId(), page);
        }
        applicationMap.put(PAGES_ATTRIBUTE, pages);
        applicationMap.put(PAGES_MAP_ATTRIBUTE, pagesMap);
        return pagesMap;
    }

    private static void addPages(boolean rootLevel, File[] files, List<Page> pages) {
        for (File file : files) {
            LOGGER.debug("file: {}", file.getAbsolutePath());
            LOGGER.debug("file name: {}", file.getName());
            if (file.getName().endsWith(".xhtml")) {
                String viewId = "/" + file.getName();
                Page page = new Page(viewId);
                pages.add(page);
            } else if (file.isDirectory()) {
                if (rootLevel) {
                    if ("WEB-INF".equals(file.getName())) {
                        continue;
                    } else if ("resources".equals(file.getName())) {
                        continue;
                    }
                }
                addPages(false, file.listFiles(), pages);
            }
        }
    }

    public static void markVisited(String viewId) {
        Map<String, Page> pages = getPagesMap();
        Page page = pages.get(viewId);
        if (null == page) {
            return;
        }
        page.setVisited(true);
    }

    public void clearPagesState() {
        Map<String, Page> pages = getPagesMap();
        for (Page page : pages.values()) {
            page.setVisited(false);
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(this.pagesDataTables.getClientId(),
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Cleared pages state.", null));
    }
}
