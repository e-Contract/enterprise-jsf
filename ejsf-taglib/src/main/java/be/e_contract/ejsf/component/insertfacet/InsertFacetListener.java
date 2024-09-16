/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.insertfacet;

import java.util.Map;
import java.util.Objects;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.PhaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertFacetListener implements ComponentSystemEventListener, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertFacetListener.class);

    private String name;

    private String targetName;

    private boolean _transient;

    public InsertFacetListener() {
        super();
    }

    public InsertFacetListener(String name, String targetName) {
        this.name = name;
        this.targetName = targetName;
    }

    public String getName() {
        return this.name;
    }

    public String getTargetName() {
        return this.targetName;
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        LOGGER.debug("event: {}", event);
        FacesContext facesContext = event.getFacesContext();
        PhaseId phaseId = facesContext.getCurrentPhaseId();
        LOGGER.debug("phase: {}", phaseId);
        LOGGER.debug("listener: {}", this);
        UIComponent component = event.getComponent();
        if (null == component) {
            LOGGER.debug("no event component");
            return;
        }
        LOGGER.debug("component: {}", component.getId());
        String targetName;
        if (null != this.targetName) {
            targetName = this.targetName;
        } else {
            targetName = this.name;
        }
        if (null != component.getFacet(targetName)) {
            LOGGER.debug("component {} already has facet {}", component.getId(), targetName);
            //return; mojarra needs to pass here
        }
        UIComponent compositeComponentParent = UIComponent.getCompositeComponentParent(component);
        LOGGER.debug("composite component parent: {}", compositeComponentParent.getId());
        Map<String, UIComponent> facets = compositeComponentParent.getFacets();
        UIComponent facet = facets.remove(this.name);
        if (null == facet) {
            LOGGER.warn("facet {} not found on composite {}", this.name, compositeComponentParent.getId());
            return;
        }
        LOGGER.debug("insert facet {} into {} as {}", this.name, component.getId(), targetName);
        LOGGER.debug("facet type: {}", facet);
        component.getFacets().put(targetName, facet);
        facet.setId(facet.getId());
    }

    @Override
    public Object saveState(FacesContext context) {
        LOGGER.debug("saveState");
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.name,
            this.targetName
        };
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        LOGGER.debug("restoreState");
        if (context == null) {
            throw new NullPointerException();
        }
        if (state == null) {
            return;
        }
        Object[] stateObjects = (Object[]) state;
        if (stateObjects.length == 0) {
            return;
        }
        this.name = (String) stateObjects[0];
        this.targetName = (String) stateObjects[1];
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        this._transient = newTransientValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.targetName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InsertFacetListener other = (InsertFacetListener) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.targetName, other.targetName);
    }
}
