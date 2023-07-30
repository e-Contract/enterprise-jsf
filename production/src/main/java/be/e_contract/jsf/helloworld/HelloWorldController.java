package be.e_contract.jsf.helloworld;

import javax.annotation.PostConstruct;
import javax.inject.Named;

@Named
public class HelloWorldController {

    private String value;

    @PostConstruct
    public void postConstruct() {
        this.value = "hello world from CDI";
    }

    public String getValue() {
        return this.value;
    }
}
