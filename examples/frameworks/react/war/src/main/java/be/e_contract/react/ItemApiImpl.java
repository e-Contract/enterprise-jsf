package be.e_contract.react;

import be.e_contract.model.ExistingItemException;
import be.e_contract.model.Model;
import be.e_contract.react.api.ItemApi;
import be.e_contract.react.api.model.AddError;
import be.e_contract.react.api.model.AddErrors;
import be.e_contract.react.api.model.Item;
import be.e_contract.react.api.model.RemoveError;
import be.e_contract.react.api.model.RemoveErrors;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

public class ItemApiImpl implements ItemApi {

    @Inject
    private Model model;

    @Override
    public void add(String name, BigDecimal amount) {
        AddErrors addErrors = new AddErrors();
        if (StringUtils.isBlank(name)) {
            addErrors.addErrorsItem(AddError.MISSING_NAME);
        }
        if (null == amount) {
            addErrors.addErrorsItem(AddError.MISSING_AMOUNT);
        } else {
            if (amount.intValue() < 1) {
                addErrors.addErrorsItem(AddError.AMOUNT_MINIMUM);
            }
        }
        // openapi-generator-maven-plugin 6.6.0 also requires
        // !addErrors.getErrors().isEmpty()
        if (null != addErrors.getErrors() && !addErrors.getErrors().isEmpty()) {
            Response response = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(addErrors)
                    .build();
            throw new WebApplicationException(response);
        }
        be.e_contract.model.Item item = new be.e_contract.model.Item();
        item.setName(name);
        item.setAmount(amount.intValue());
        try {
            this.model.addItem(item);
        } catch (ExistingItemException ex) {
            addErrors.addErrorsItem(AddError.EXISTING_NAME);
            Response response = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(addErrors)
                    .build();
            throw new WebApplicationException(response);
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
        if (StringUtils.isBlank(name)) {
            RemoveErrors removeErrors = new RemoveErrors();
            removeErrors.addErrorsItem(RemoveError.MISSING_NAME);
            Response response = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(removeErrors)
                    .build();
            throw new WebApplicationException(response);
        }
        List<be.e_contract.model.Item> items = this.model.getItems();
        be.e_contract.model.Item removeItem = null;
        for (be.e_contract.model.Item item : items) {
            if (item.getName().equals(name)) {
                removeItem = item;
                break;
            }
        }
        if (null == removeItem) {
            RemoveErrors removeErrors = new RemoveErrors();
            removeErrors.addErrorsItem(RemoveError.UNKNOWN_NAME);
            Response response = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(removeErrors)
                    .build();
            throw new WebApplicationException(response);
        }
        this.model.removeItem(removeItem);
    }

}
