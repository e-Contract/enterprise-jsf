package be.e_contract.jsf.helloworld;

import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;

@Named("echartsController")
public class EChartsController {

    public String getValue() {
        InputStream inputStream
                = EChartsController.class
                        .getResourceAsStream("/echarts.json");
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException ex) {
            return "";
        }
    }
}
