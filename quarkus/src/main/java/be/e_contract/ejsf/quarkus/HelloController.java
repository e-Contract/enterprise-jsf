package be.e_contract.ejsf.quarkus;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class HelloController implements Serializable {

    public String getValue() {
        return "hello world";
    }
}
