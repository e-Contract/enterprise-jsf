package be.e_contract.jsf.helloworld;

import be.e_contract.jsf.taglib.output.ExampleEnum;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;

@Named
public class EnumOutputController {

    private List<ExampleEnum> items;

    @PostConstruct
    public void postConstruct() {
        this.items = new LinkedList<>();
        for (int idx = 0; idx < 10; idx++) {
            ExampleEnum exampleEnum = ExampleEnum.values()[idx % 3];
            this.items.add(exampleEnum);
        }
    }

    public List<ExampleEnum> getItems() {
        return this.items;
    }
}
