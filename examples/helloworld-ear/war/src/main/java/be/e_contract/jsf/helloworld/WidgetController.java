package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class WidgetController implements Serializable {

    private String value;

    private boolean rendered;

    @PostConstruct
    public void postConstruct() {
        this.value = "initial value";
        this.rendered = true;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isRendered() {
        return this.rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public void updateValue() {
        this.value = LocalDateTime.now().toString();
    }
}
