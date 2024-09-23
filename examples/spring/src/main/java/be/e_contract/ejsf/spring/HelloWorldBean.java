package be.e_contract.ejsf.spring;

import java.io.Serializable;
import jakarta.faces.view.ViewScoped;
import org.springframework.stereotype.Component;

@Component
@ViewScoped
public class HelloWorldBean implements Serializable {

    public String getValue() {
        return "hello world";
    }
}
