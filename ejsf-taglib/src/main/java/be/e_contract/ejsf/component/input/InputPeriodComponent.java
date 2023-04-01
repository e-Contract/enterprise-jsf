/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.input;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(InputPeriodComponent.COMPONENT_TYPE)
public class InputPeriodComponent extends UIInput implements NamingContainer {

    public static final String COMPONENT_TYPE = "ejsf.inputPeriodComponent";

    private static final Logger LOGGER = LoggerFactory.getLogger(InputPeriodComponent.class);

    private UIInput years;
    private UIInput months;
    private UIInput monthsRange;
    private UIInput days;
    private UIInput daysRange;
    private UIInput hours;
    private UIInput hoursRange;
    private UIInput minutes;
    private UIInput minutesRange;
    private UIInput seconds;
    private UIInput secondsRange;

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    public UIInput getHours() {
        return this.hours;
    }

    public void setHours(UIInput hours) {
        this.hours = hours;
    }

    public UIInput getDays() {
        return this.days;
    }

    public void setDays(UIInput days) {
        this.days = days;
    }

    public UIInput getMinutes() {
        return this.minutes;
    }

    public void setMinutes(UIInput minutes) {
        this.minutes = minutes;
    }

    public UIInput getSeconds() {
        return this.seconds;
    }

    public void setSeconds(UIInput seconds) {
        this.seconds = seconds;
    }

    public UIInput getYears() {
        return this.years;
    }

    public void setYears(UIInput years) {
        this.years = years;
    }

    public UIInput getMonths() {
        return this.months;
    }

    public void setMonths(UIInput months) {
        this.months = months;
    }

    public UIInput getMonthsRange() {
        return this.monthsRange;
    }

    public void setMonthsRange(UIInput monthsRange) {
        this.monthsRange = monthsRange;
    }

    public UIInput getDaysRange() {
        return this.daysRange;
    }

    public void setDaysRange(UIInput daysRange) {
        this.daysRange = daysRange;
    }

    public UIInput getHoursRange() {
        return this.hoursRange;
    }

    public void setHoursRange(UIInput hoursRange) {
        this.hoursRange = hoursRange;
    }

    public UIInput getMinutesRange() {
        return this.minutesRange;
    }

    public void setMinutesRange(UIInput minutesRange) {
        this.minutesRange = minutesRange;
    }

    public UIInput getSecondsRange() {
        return this.secondsRange;
    }

    public void setSecondsRange(UIInput secondsRange) {
        this.secondsRange = secondsRange;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        Integer total = (Integer) getSubmittedValue();
        if (null == total) {
            total = (Integer) getValue();
        }
        Integer totalSeconds;
        if (null == total) {
            totalSeconds = null;
        } else {
            ChronoUnit unit = getUnit();
            switch (unit) {
                case YEARS:
                    totalSeconds = total * 12 * 30 * 24 * 60 * 60;
                    break;
                case MONTHS:
                    totalSeconds = total * 30 * 24 * 60 * 60;
                    break;
                case DAYS:
                    totalSeconds = total * 24 * 60 * 60;
                    break;
                case HOURS:
                    totalSeconds = total * 60 * 60;
                    break;
                case MINUTES:
                    totalSeconds = total * 60;
                    break;
                case SECONDS:
                    totalSeconds = total;
                    break;
                default:
                    throw new IOException("unsupported unit: " + unit);
            }
        }
        if (null == totalSeconds) {
            Stream<UIInput> inputsStream = getInputsStream();
            inputsStream.forEach(input -> input.setValue(null));
        } else {
            LOGGER.debug("total seconds: {}", totalSeconds);
            int secondsValue = totalSeconds % 60;
            int minutesLeft = totalSeconds / 60;

            int minutesValue = minutesLeft % 60;
            int hoursLeft = minutesLeft / 60;

            int hoursValue = hoursLeft % 24;
            int daysLeft = hoursLeft / 24;

            int daysValue = daysLeft % 30;
            int monthsLeft = daysLeft / 30;

            int monthsValue = monthsLeft % 12;
            int yearsValue = monthsLeft / 12;

            if (isAllValid()) {
                this.seconds.setValue(secondsValue);
                this.secondsRange.setValue(secondsValue);

                this.minutes.setValue(minutesValue);
                this.minutesRange.setValue(minutesValue);

                this.hours.setValue(hoursValue);
                this.hoursRange.setValue(hoursValue);

                this.days.setValue(daysValue);
                this.daysRange.setValue(daysValue);

                this.months.setValue(monthsValue);
                this.monthsRange.setValue(monthsValue);

                this.years.setValue(yearsValue);
            }
            if (!isValid()) {
                Stream<UIInput> inputsStream = getInputsStream();
                inputsStream.forEach(input -> input.setValid(false));
            }
        }
        super.encodeBegin(context);
    }

    private Stream<UIInput> getInputsStream() {
        UIInput[] inputs = new UIInput[]{
            this.seconds,
            this.secondsRange,
            this.minutes,
            this.minutesRange,
            this.hours,
            this.hoursRange,
            this.days,
            this.daysRange,
            this.months,
            this.monthsRange,
            this.years
        };
        Stream<UIInput> stream = Arrays.stream(inputs);
        return stream;
    }

    private boolean isAllValid() {
        Stream<UIInput> inputsStream = getInputsStream();
        boolean anyInputInvalid = inputsStream.anyMatch(input -> !input.isValid());
        return !anyInputInvalid;
    }

    @Override
    public void processDecodes(FacesContext context) {
        super.processDecodes(context);

        int yearsValue;
        int monthsValue;
        int daysValue;
        int hoursValue;
        int minutesValue;
        int secondsValue;

        try {
            yearsValue = getIntValue(this.years, null);
            monthsValue = getIntValue(this.months, this.monthsRange);
            daysValue = getIntValue(this.days, this.daysRange);
            hoursValue = getIntValue(this.hours, this.hoursRange);
            minutesValue = getIntValue(this.minutes, this.minutesRange);
            secondsValue = getIntValue(this.seconds, this.secondsRange);
        } catch (NumberFormatException e) {
            return;
        }

        int totalSeconds = yearsValue;
        totalSeconds = totalSeconds * 12 + monthsValue;
        totalSeconds = totalSeconds * 30 + daysValue;
        totalSeconds = totalSeconds * 24 + hoursValue;
        totalSeconds = totalSeconds * 60 + minutesValue;
        totalSeconds = totalSeconds * 60 + secondsValue;

        int total;
        ChronoUnit unit = getUnit();
        switch (unit) {
            case YEARS:
                total = totalSeconds / (12 * 30 * 24 * 60 * 60);
                break;
            case MONTHS:
                total = totalSeconds / (30 * 24 * 60 * 60);
                break;
            case DAYS:
                total = totalSeconds / (24 * 60 * 60);
                break;
            case HOURS:
                total = totalSeconds / (60 * 60);
                break;
            case MINUTES:
                total = totalSeconds / 60;
                break;
            case SECONDS:
                total = totalSeconds;
                break;
            default:
                total = totalSeconds;
        }
        setSubmittedValue(total);
    }

    private int getIntValue(UIInput input, UIInput input2) {
        String submittedValue = (String) input.getSubmittedValue();
        if (UIInput.isEmpty(submittedValue)) {
            if (null != input2) {
                submittedValue = (String) input2.getSubmittedValue();
            } else {
                return 0;
            }
        }
        if (UIInput.isEmpty(submittedValue)) {
            return 0;
        } else {
            return Integer.parseInt(submittedValue);
        }
    }

    public List<SelectItem> getSelectItems(int max) {
        List<SelectItem> selectItems = new LinkedList<>();
        for (int idx = 0; idx < max; idx++) {
            SelectItem selectItem = new SelectItem(idx);
            selectItems.add(selectItem);
        }
        return selectItems;
    }

    public List<SelectItem> getHoursSelectItems() {
        return getSelectItems(24);
    }

    public List<SelectItem> getMinutesSelectItems() {
        return getSelectItems(60);
    }

    public List<SelectItem> getSecondsSelectItems() {
        return getSelectItems(60);
    }

    public List<SelectItem> getDaysSelectItems() {
        return getSelectItems(30);
    }

    public List<SelectItem> getMonthsSelectItems() {
        return getSelectItems(12);
    }

    private ChronoUnit getMaxUnit() {
        String maxUnit = (String) getAttributes().get("maxUnit");
        if (UIInput.isEmpty(maxUnit)) {
            maxUnit = ChronoUnit.MONTHS.name();
        }
        return ChronoUnit.valueOf(maxUnit);
    }

    private ChronoUnit getUnit() {
        String unit = (String) getAttributes().get("unit");
        if (UIInput.isEmpty(unit)) {
            unit = ChronoUnit.SECONDS.name();
        }
        return ChronoUnit.valueOf(unit);
    }

    private static final Map<ChronoUnit, Integer> chronoUnitOrder;

    static {
        chronoUnitOrder = new HashMap<>();
        for (int idx = 0; idx < ChronoUnit.values().length; idx++) {
            ChronoUnit chronoUnit = ChronoUnit.values()[idx];
            chronoUnitOrder.put(chronoUnit, idx);
        }
    }

    private boolean inRange(ChronoUnit chronoUnit) {
        ChronoUnit unit = getUnit();
        ChronoUnit maxUnit = getMaxUnit();
        int unitOrder = chronoUnitOrder.get(unit);
        int maxUnitOrder = chronoUnitOrder.get(maxUnit);
        int order = chronoUnitOrder.get(chronoUnit);
        if (order > maxUnitOrder) {
            return false;
        }
        if (order < unitOrder) {
            return false;
        }
        return true;
    }

    private boolean isRender(ChronoUnit chronoUnit) {
        if (!inRange(chronoUnit)) {
            return false;
        }
        return getMaxUnit() == chronoUnit;
    }

    public boolean isRenderYears() {
        return inRange(ChronoUnit.YEARS);
    }

    public boolean isRenderMonths() {
        return isRender(ChronoUnit.MONTHS);
    }

    public boolean isRenderDays() {
        return isRender(ChronoUnit.DAYS);
    }

    public boolean isRenderHours() {
        return isRender(ChronoUnit.HOURS);
    }

    public boolean isRenderMinutes() {
        return isRender(ChronoUnit.MINUTES);
    }

    public boolean isRenderSeconds() {
        return isRender(ChronoUnit.SECONDS);
    }

    private boolean isRenderRange(ChronoUnit chronoUnit) {
        if (!inRange(chronoUnit)) {
            return false;
        }
        return getMaxUnit() != chronoUnit;
    }

    public boolean isRenderMonthsRange() {
        return isRenderRange(ChronoUnit.MONTHS);
    }

    public boolean isRenderDaysRange() {
        return isRenderRange(ChronoUnit.DAYS);
    }

    public boolean isRenderHoursRange() {
        return isRenderRange(ChronoUnit.HOURS);
    }

    public boolean isRenderMinutesRange() {
        return isRenderRange(ChronoUnit.MINUTES);
    }

    public boolean isRenderSecondsRange() {
        return isRenderRange(ChronoUnit.SECONDS);
    }
}
