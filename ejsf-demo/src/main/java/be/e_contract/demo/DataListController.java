/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named("dataListController")
@ViewScoped
public class DataListController implements Serializable {

    public static class Item implements Serializable {

        private int value1;
        private int value2;

        public Item(int value1, int value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public int getValue1() {
            return this.value1;
        }

        public void setValue1(int value1) {
            this.value1 = value1;
        }

        public int getValue2() {
            return this.value2;
        }

        public void setValue2(int value2) {
            this.value2 = value2;
        }

        @Override
        public String toString() {
            return "Item{" + "value1=" + value1 + ", value2=" + value2 + '}';
        }
    }

    private List<Item> items;

    @PostConstruct
    public void postConstruct() {
        this.items = new LinkedList<>();
        this.items.add(new Item(10, 1));
        this.items.add(new Item(20, 2));
        this.items.add(new Item(30, 3));
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void save() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        for (Item item : this.items) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    item.value1 + " = " + item.value2, null);
            facesContext.addMessage(null, facesMessage);
        }
    }

    public void add() {
        this.items.add(new Item(0, 0));
    }
}
