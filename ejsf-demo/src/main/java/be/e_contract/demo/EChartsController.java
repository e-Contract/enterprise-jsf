/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.output.echarts.EChartsClickEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;

@Named("echartsController")
public class EChartsController {

    public String getValue() {
        InputStream inputStream
                = EChartsController.class.getResourceAsStream("/echarts.json");
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException ex) {
            return "";
        }
    }

    public void handleClick(EChartsClickEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "Clicked " + event.getName()
                + ", series index " + event.getSeriesIndex()
                + ", data index " + event.getDataIndex();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, message, null);
        facesContext.addMessage(null, facesMessage);
    }
}
