package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class GenericConverterController implements Serializable {

    public static class Item implements Serializable {

        private final String id;
        private final String name;

        public Item(String name) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private List<Item> items;

    private Item item;

    @PostConstruct
    public void postConstruct() {
        this.items = new LinkedList<>();
        this.items.add(new Item("Hello"));
        this.items.add(new Item("World"));
    }

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<SelectItem> getItems() {
        List<SelectItem> selectItems = new LinkedList<>();
        for (Item item : this.items) {
            SelectItem selectItem = new SelectItem(item, item.getName());
            selectItems.add(selectItem);
        }
        return selectItems;
    }

    public String getAsString(Object object) {
        if (null == object) {
            return null;
        }
        Item item = (Item) object;
        return item.getId();
    }

    public Object getAsObject(String value) {
        for (Item item : this.items) {
            if (item.getId().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
