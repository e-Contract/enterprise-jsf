package be.e_contract.jsf.helloworld;

import java.io.IOException;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;

@Named("echartsController")
public class EChartsController {

    public String getValue() {
        try {
            return IOUtils.toString(EChartsController.class.getResourceAsStream("/echarts.json"), "UTF-8");
        } catch (IOException ex) {
            return "";
        }
    }
}
