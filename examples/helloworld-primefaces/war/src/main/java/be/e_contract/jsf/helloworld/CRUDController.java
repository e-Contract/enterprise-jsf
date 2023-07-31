package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;

@Named("crudController")
@ViewScoped
public class CRUDController implements Serializable {

    public static class Item implements Serializable {

        private String name;
        private int amount;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAmount() {
            return this.amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    private final List<Item> items = new LinkedList<>();

    public List<Item> getItems() {
        return this.items;
    }

    private Item newItem;

    public void initAdd() {
        this.newItem = new Item();
    }

    public Item getNewItem() {
        return this.newItem;
    }

    public void add() {
        this.items.add(this.newItem);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Item " + this.newItem.getName() + " added.", null);
        facesContext.addMessage(null, facesMessage);
        this.newItem = null;
        PrimeFaces primeFaces = PrimeFaces.current();
        primeFaces.ajax().addCallbackParam("itemAdded", true);
    }

    private Item selectedItem;

    public void selectItem(Item item) {
        this.selectedItem = item;
    }

    public Item getSelectedItem() {
        return this.selectedItem;
    }

    public void removeSelectedItem() {
        this.items.remove(this.selectedItem);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Item " + this.selectedItem.getName() + " removed.", null);
        facesContext.addMessage(null, facesMessage);
        this.selectedItem = null;
    }
}
