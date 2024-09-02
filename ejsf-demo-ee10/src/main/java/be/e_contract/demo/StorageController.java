/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.storage.StorageGetItemsEvent;
import java.io.Serializable;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("storageController")
@ViewScoped
public class StorageController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageController.class);

    private String value1;
    private String value2;

    public String getValue1() {
        return this.value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return this.value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public void itemsListener(StorageGetItemsEvent event) {
        LOGGER.debug("items listener");
        LOGGER.debug("item 1: {}", event.getItem("item1"));
        LOGGER.debug("item 2: {}", event.getItem("item2"));
    }
}
