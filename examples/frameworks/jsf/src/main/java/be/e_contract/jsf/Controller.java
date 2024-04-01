package be.e_contract.jsf;

import be.e_contract.ejsf.Utils;
import be.e_contract.model.ExistingItemException;
import be.e_contract.model.Item;
import be.e_contract.model.Model;
import java.io.Serializable;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

@Named("controller")
@ViewScoped
public class Controller implements Serializable {

    private List<Item> items;

    @Inject
    private Model model;

    @PostConstruct
    public void postConstruct() {
        this.items = this.model.getItems();
    }

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
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            this.items = this.model.addItem(this.newItem);
        } catch (ExistingItemException ex) {
            FacesMessage facesMessage
                    = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Existing item.", null);
            Utils.invalidateInput(facesMessage, "name");
            return;
        }
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Item " + this.newItem.getName() + " added.", null);
        facesContext.addMessage(null, facesMessage);
        this.newItem = null;
        PrimeFaces primeFaces = PrimeFaces.current();
        PrimeFaces.Ajax ajax = primeFaces.ajax();
        ajax.addCallbackParam("itemAdded", true);
    }

    private Item selectedItem;

    public void selectItem(Item item) {
        this.selectedItem = item;
    }

    public Item getSelectedItem() {
        return this.selectedItem;
    }

    public void removeSelectedItem() {
        this.items = this.model.removeItem(this.selectedItem);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Item " + this.selectedItem.getName() + " removed.", null);
        facesContext.addMessage(null, facesMessage);
        this.selectedItem = null;
    }
}
