package be.e_contract.model;

import java.util.LinkedList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Model {

    private final List<Item> items = new LinkedList<>();

    public List<Item> getItems() {
        return this.items;
    }

    public List<Item> addItem(Item item) throws ExistingItemException {
        if (this.items.contains(item)) {
            throw new ExistingItemException();
        }
        this.items.add(item);
        return this.items;
    }

    public List<Item> removeItem(Item item) {
        this.items.remove(item);
        return this.items;
    }
}
