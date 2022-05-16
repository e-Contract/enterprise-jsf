package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class DataController implements Serializable {

    public static class Item implements Serializable {

        private final String name;
        private final String value;

        public Item(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }
    }

    private List<Item> items;

    @PostConstruct
    public void postConstruct() {
        this.items = new LinkedList<>();
        this.items.add(new Item("item 1", "value 1"));
        this.items.add(new Item("item 2", "value 2"));
    }

    public List<Item> getItems() {
        return this.items;
    }
}
