package be.e_contract.ejsf.quarkus;

import java.io.Serializable;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class HelloController implements Serializable {

    public String getValue() {
        return "hello world";
    }
}
