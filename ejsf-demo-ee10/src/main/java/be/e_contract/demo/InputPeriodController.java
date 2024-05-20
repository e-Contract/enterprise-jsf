/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named("inputPeriodController")
@ViewScoped
public class InputPeriodController implements Serializable {

    @PostConstruct
    public void postConstruct() {
        this.unit = ChronoUnit.SECONDS.name();
        this.maxUnit = ChronoUnit.MONTHS.name();
    }

    private Integer period;

    private String unit;

    private String maxUnit;

    private boolean disabled;

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    private SelectItem getSelectItem(ChronoUnit chronoUnit) {
        return new SelectItem(chronoUnit.name(), chronoUnit.toString());
    }

    public List<SelectItem> getUnitSelectItems() {
        List<SelectItem> selectItems = new LinkedList<>();
        selectItems.add(getSelectItem(ChronoUnit.SECONDS));
        selectItems.add(getSelectItem(ChronoUnit.MINUTES));
        selectItems.add(getSelectItem(ChronoUnit.HOURS));
        selectItems.add(getSelectItem(ChronoUnit.DAYS));
        selectItems.add(getSelectItem(ChronoUnit.MONTHS));
        selectItems.add(getSelectItem(ChronoUnit.YEARS));
        return selectItems;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMaxUnit() {
        return this.maxUnit;
    }

    public void setMaxUnit(String maxUnit) {
        this.maxUnit = maxUnit;
    }

    public void update() {
        this.period = null;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
