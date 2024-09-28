/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.output.carousel.CarouselImage;
import be.e_contract.ejsf.component.performance.PerformanceNavigationTimingEvent;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@SessionScoped
public class CarouselPerformanceController implements Serializable {

    private int carouselCount;

    private int imageCount;

    private int imageDelay;

    @PostConstruct
    public void postConstruct() {
        this.carouselCount = 150;
        this.imageCount = 10;
        this.imageDelay = 0;
    }

    public void initView() {
        ImageServlet.setDelay(this.imageDelay);
    }

    public Integer[] getIndexes() {
        Integer[] indexes = new Integer[150];
        for (int idx = 0; idx < indexes.length; idx++) {
            indexes[idx] = idx;
        }
        return indexes;
    }

    public List<CarouselImage> getCarouselImages(int index) {
        List<CarouselImage> carouselImages = new LinkedList<>();
        for (int idx = 0; idx < 10; idx++) {
            int imageIdx = index * 10000 + idx;
            CarouselImage carouselImage = new CarouselImage("./demo/image/" + imageIdx, "./demo/image/thumbnail/" + imageIdx);
            carouselImage.withZoomImage("./demo/image/hd/" + imageIdx);
            if (idx % 2 == 0) {
                carouselImage.withCaption("A description for image " + imageIdx);
            }
            carouselImages.add(carouselImage);
        }
        return carouselImages;
    }

    public void handleEvent(PerformanceNavigationTimingEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "start time: " + event.getStartTime(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "duration: " + event.getDuration(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "request start: " + event.getRequestStart(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "response start: " + event.getResponseStart(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "response end: " + event.getResponseEnd(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "DOM interactive: " + event.getDomInteractive(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "DOM complete: " + event.getDomComplete(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "load event start: " + event.getLoadEventStart(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "load event end: " + event.getLoadEventEnd(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "name: " + event.getName(), null));
    }

    public int getCarouselCount() {
        return this.carouselCount;
    }

    public void setCarouselCount(int carouselCount) {
        this.carouselCount = carouselCount;
    }

    public int getImageCount() {
        return this.imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public int getImageDelay() {
        return this.imageDelay;
    }

    public void setImageDelay(int imageDelay) {
        this.imageDelay = imageDelay;
    }

    public void save() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Settings saved in session.", null));
        ImageServlet.setDelay(this.imageDelay);
    }
}
