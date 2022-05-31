package test.unit.be.e_contract.jsf.taglib.validator;

import javax.annotation.PostConstruct;
import javax.inject.Named;

@Named
public class TestController {

    private String value;

    @PostConstruct
    public void postConstruct() {
        this.value = "hello world";
    }

    public String getValue() {
        return this.value;
    }
}
