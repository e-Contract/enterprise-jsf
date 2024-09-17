package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class WidgetController implements Serializable {

    private boolean rendered;

    private String value;

    @PostConstruct
    public void postConstruct() {
        this.rendered = true;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRendered() {
        return this.rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
}
