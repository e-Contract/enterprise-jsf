package be.e_contract.jsf.helloworld;

import be.e_contract.jsf.taglib.table.TableModel;
import java.util.LinkedList;
import java.util.List;

public class SimpleTableModel implements TableModel {

    private static final int PAGE_SIZE = 5;

    @Override
    public int getPageCount() {
        return 10;
    }

    @Override
    public List<String> getPage(int pageIdx) {
        List<String> page = new LinkedList<>();
        for (int idx = 0; idx < PAGE_SIZE; idx++) {
            page.add("Item " + pageIdx + "." + idx);
        }
        return page;
    }
}
