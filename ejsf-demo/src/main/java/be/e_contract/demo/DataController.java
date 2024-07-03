/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named("dataController")
@ViewScoped
public class DataController implements Serializable {

    public static class Item implements Serializable {

        private final String name;
        private final String type;
        private Object value;

        public Item(String name, String type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getType() {
            return this.type;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    private List<Item> items;

    @PostConstruct
    public void postConstruct() {
        this.items = new LinkedList<>();
        this.items.add(new Item("item 1", "string", "value 1"));
        this.items.add(new Item("item 2", "bool", false));
        this.items.add(new Item("Item 3", "date", new Date()));
        this.items.add(new Item("Item 4", "number", 10));
        this.items.add(new Item("Item 4", "period", 10));
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void save() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        for (Item item : this.items) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    item.name + " = " + item.value, null);
            facesContext.addMessage(null, facesMessage);
        }
    }
}
