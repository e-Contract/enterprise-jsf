/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.performance;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.AjaxBehaviorEvent;

public class PerformanceNavigationTimingEvent extends AjaxBehaviorEvent {

    public static final String NAME = "timing";

    private double startTime;

    private double duration;

    private double loadEventStart;

    private double loadEventEnd;

    private double domInteractive;

    private double domComplete;

    private double requestStart;

    private double responseStart;

    private double responseEnd;

    public PerformanceNavigationTimingEvent(UIComponent component, Behavior behavior) {
        super(component, behavior);
    }

    public double getStartTime() {
        return this.startTime;
    }

    public double getDuration() {
        return this.duration;
    }

    void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    void setDuration(double duration) {
        this.duration = duration;
    }

    public double getLoadEventStart() {
        return this.loadEventStart;
    }

    public double getLoadEventEnd() {
        return this.loadEventEnd;
    }

    void setLoadEventStart(double loadEventStart) {
        this.loadEventStart = loadEventStart;
    }

    void setLoadEventEnd(double loadEventEnd) {
        this.loadEventEnd = loadEventEnd;
    }

    public double getDomInteractive() {
        return this.domInteractive;
    }

    void setDomInteractive(double domInteractive) {
        this.domInteractive = domInteractive;
    }

    public double getDomComplete() {
        return this.domComplete;
    }

    void setDomComplete(double domComplete) {
        this.domComplete = domComplete;
    }

    public double getRequestStart() {
        return this.requestStart;
    }

    void setRequestStart(double requestStart) {
        this.requestStart = requestStart;
    }

    public double getResponseStart() {
        return this.responseStart;
    }

    void setResponseStart(double responseStart) {
        this.responseStart = responseStart;
    }

    public double getResponseEnd() {
        return this.responseEnd;
    }

    void setResponseEnd(double responseEnd) {
        this.responseEnd = responseEnd;
    }
}
