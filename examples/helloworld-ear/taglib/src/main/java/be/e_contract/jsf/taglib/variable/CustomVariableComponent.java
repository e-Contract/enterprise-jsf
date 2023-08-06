package be.e_contract.jsf.taglib.variable;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

@FacesComponent(CustomVariableComponent.COMPONENT_TYPE)
public class CustomVariableComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.customVariable";

    public static final String COMPONENT_FAMILY = "ejsf";

    private static final String VAR_STACK_ATTRIBUTE
            = CustomVariableComponent.class.getName() + ".var";

    public CustomVariableComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        var,
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
    }

    public String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var);
    }

    @Override
    public void pushComponentToEL(FacesContext context, UIComponent component) {
        super.pushComponentToEL(context, component);
        Map<Object, Object> contextAttributes = context.getAttributes();
        Deque<ValueExpression> varStack = (Deque<ValueExpression>) contextAttributes.get(VAR_STACK_ATTRIBUTE);
        if (null == varStack) {
            varStack = new LinkedList<>();
            contextAttributes.put(VAR_STACK_ATTRIBUTE, varStack);
        }
        String var = getVar();
        ELContext elContext = context.getELContext();
        VariableMapper variableMapper = elContext.getVariableMapper();
        ValueExpression valueExpression = variableMapper.resolveVariable(var);
        varStack.push(valueExpression);
        Application application = context.getApplication();
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        valueExpression = expressionFactory.createValueExpression(LocalDateTime.now().toString(), String.class);
        variableMapper.setVariable(var, valueExpression);
        try {
            Thread.sleep(5);
        } catch (InterruptedException ex) {
        }
    }

    @Override
    public void popComponentFromEL(FacesContext context) {
        super.popComponentFromEL(context);
        Map<Object, Object> contextAttributes = context.getAttributes();
        Deque<ValueExpression> varStack = (Deque<ValueExpression>) contextAttributes.get(VAR_STACK_ATTRIBUTE);
        ValueExpression valueExpression = varStack.pop();
        ELContext elContext = context.getELContext();
        VariableMapper variableMapper = elContext.getVariableMapper();
        String var = getVar();
        variableMapper.setVariable(var, valueExpression);
    }
}
