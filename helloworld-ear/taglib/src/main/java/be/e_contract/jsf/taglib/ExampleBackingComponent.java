package be.e_contract.jsf.taglib;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@FacesComponent("ejsf.exampleBackingComponent")
public class ExampleBackingComponent extends UIInput implements NamingContainer {

    private UIInput hours;
    private UIInput days;

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


    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        Integer totalHours = (Integer) getValue();
        if (null == totalHours) {
            this.days.setValue(null);
            this.hours.setValue(null);
        } else {
            int restHours = totalHours % (30 * 24);
            int daysValue = restHours / 24;
            int hoursValue = restHours % 24;
            this.days.setValue(daysValue);
            this.hours.setValue(hoursValue);
        }
        super.encodeBegin(context);
    }

    @Override
    public Object getSubmittedValue() {
        String daysSubmittedValue = (String) this.days.getSubmittedValue();
        int daysValue;
        if (UIInput.isEmpty(daysSubmittedValue)) {
            daysValue = 0;
        } else {
            daysValue = Integer.parseInt(daysSubmittedValue);
        }

        int hoursValue;
        String hoursSubmittedValue = (String) this.hours.getSubmittedValue();
        if (UIInput.isEmpty(hoursSubmittedValue)) {
            hoursValue = 0;
        } else {
            hoursValue = Integer.parseInt(hoursSubmittedValue);
        }

        return daysValue * 24 + hoursValue;
    }

    public List<SelectItem> getHoursSelectItems() {
        List<SelectItem> selectItems = new LinkedList<>();
        for (int idx = 0; idx < 24; idx++) {
            SelectItem selectItem = new SelectItem(idx);
            selectItems.add(selectItem);
        }
        return selectItems;
    }
}
