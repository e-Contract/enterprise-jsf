/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.quarkus;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ViewScoped
public class HelloController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);

    private String value;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private List<HelloEntity> helloList;

    @Inject
    EntityManager entityManager;

    @PostConstruct
    public void postConstruct() {
        Query query = this.entityManager.createQuery("SELECT he FROM HelloEntity AS he");
        this.helloList = query.getResultList();
    }

    public List<HelloEntity> getHelloList() {
        return this.helloList;
    }

    private HelloEntity newHello;

    public void initAdd() {
        this.newHello = new HelloEntity();
    }

    public HelloEntity getNewHello() {
        return this.newHello;
    }

    @Transactional
    public void addHello() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HelloEntity existingHelloEntity = this.entityManager.find(HelloEntity.class, this.newHello.getName());
        if (null != existingHelloEntity) {
            facesContext.addMessage("addDialogForm:name", new FacesMessage(FacesMessage.SEVERITY_ERROR, "existing entity", null));
            return;
        }
        this.entityManager.persist(this.newHello);
        Query query = this.entityManager.createQuery("SELECT he FROM HelloEntity AS he");
        this.helloList = query.getResultList();
        PrimeFaces.current().ajax().addCallbackParam("helloAdded", true);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "entity added: " + this.newHello.getName(), null));
        this.newHello = null;
    }

    @Transactional
    public void removeItem(HelloEntity hello) {
        HelloEntity helloEntity = this.entityManager.find(HelloEntity.class, hello.getName());
        this.entityManager.remove(helloEntity);
        Query query = this.entityManager.createQuery("SELECT he FROM HelloEntity AS he");
        this.helloList = query.getResultList();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "entity removed: " + hello.getName(), null));
    }
}
