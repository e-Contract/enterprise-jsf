package be.e_contract.jsf.taglib.table;

import java.util.List;

public interface TableModel {

    int getPageCount();

    List<String> getPage(int pageIdx);
}
