package be.e_contract.jsf.helloworld;

import be.e_contract.jsf.taglib.table.TableModel;
import javax.inject.Named;

@Named
public class TableController {

    public TableModel getTableModel() {
        return new SimpleTableModel();
    }
}
