package be.e_contract.react;

import be.e_contract.model.ExistingItemException;
import be.e_contract.model.Model;
import be.e_contract.react.api.ItemApi;
import be.e_contract.react.api.model.Item;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ItemApiImpl implements ItemApi {

    @Inject
    private Model model;

    @Override
    public void add(String name, BigDecimal amount) {
        be.e_contract.model.Item item = new be.e_contract.model.Item();
        item.setName(name);
        item.setAmount(amount.intValue());
        try {
            this.model.addItem(item);
        } catch (ExistingItemException ex) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    @Override
    public List<Item> callList() {
        List<be.e_contract.model.Item> items = this.model.getItems();
        List<Item> resultList = new LinkedList<>();
        for (be.e_contract.model.Item item : items) {
            Item jsonItem = new Item();
            jsonItem.setName(item.getName());
            jsonItem.setAmount(BigDecimal.valueOf(item.getAmount()));
            resultList.add(jsonItem);
        }
        return resultList;
    }

    @Override
    public void remove(String name) {
        List<be.e_contract.model.Item> items = this.model.getItems();
        be.e_contract.model.Item removeItem = null;
        for (be.e_contract.model.Item item : items) {
            if (item.getName().equals(name)) {
                removeItem = item;
                break;
            }
        }
        if (null == removeItem) {
            return;
        }
        this.model.removeItem(removeItem);
    }

}
