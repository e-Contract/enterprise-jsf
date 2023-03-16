package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class DataController implements Serializable {

    public static class Item<T> implements Serializable {

        private final String name;
        private final Class<T> type;
        private T value;

        public Item(String name, Class<T> type, T value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public Class<T> getType() {
            return this.type;
        }

        public T getValue() {
            return this.value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    private List<Item> items;

    @PostConstruct
    public void postConstruct() {
        this.items = new LinkedList<>();
        this.items.add(new Item("item 1", String.class, "value 1"));
        this.items.add(new Item("item 2", Boolean.class, false));
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void save() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        for (Item item : this.items) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "item value: " + item.value, null);
            facesContext.addMessage(null, facesMessage);
        }
    }
}
