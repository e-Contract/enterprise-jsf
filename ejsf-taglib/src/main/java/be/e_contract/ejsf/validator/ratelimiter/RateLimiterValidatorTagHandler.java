/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import java.io.IOException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.ActionSource2;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;
import javax.faces.view.facelets.ValidatorConfig;

public class RateLimiterValidatorTagHandler extends TagHandler {

    public RateLimiterValidatorTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    public RateLimiterValidatorTagHandler(ValidatorConfig validatorConfig) {
        super(validatorConfig);
    }

    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        TagAttribute forTagAttribute = getRequiredAttribute("for");
        String forValue = forTagAttribute.getValue();
        ValueExpression forValueExpression;
        if (forValue.contains("#{")) {
            forValueExpression = forTagAttribute.getValueExpression(context, String.class);
        } else {
            forValueExpression = null;
        }
        int timeoutDuration = getIntegerAttributeValue("timeoutDuration", 30, context);
        int limitRefreshPeriod = getIntegerAttributeValue("limitRefreshPeriod", 10, context);
        int limitForPeriod = getIntegerAttributeValue("limitForPeriod", 5, context);

        ValueExpression messageValueExpression;
        TagAttribute messageTagAttribute = getAttribute("message");
        if (null != messageTagAttribute) {
            messageValueExpression = messageTagAttribute.getValueExpression(context, String.class);
        } else {
            messageValueExpression = null;
        }

        MethodExpression onLimitMethodExpression;
        TagAttribute onLimitTagAttribute = getAttribute("onLimit");
        if (null != onLimitTagAttribute) {
            onLimitMethodExpression = onLimitTagAttribute.getMethodExpression(context, Object.class, new Class[]{String.class});
        } else {
            onLimitMethodExpression = null;
        }

        FacesContext facesContext = context.getFacesContext();
        Application application = facesContext.getApplication();
        RateLimiterValidator rateLimiterValidator = (RateLimiterValidator) application.createValidator(RateLimiterValidator.VALIDATOR_ID);
        rateLimiterValidator.setFor(forValue);
        rateLimiterValidator.setTimeoutDuration(timeoutDuration);
        rateLimiterValidator.setLimitRefreshPeriod(limitRefreshPeriod);
        rateLimiterValidator.setLimitForPeriod(limitForPeriod);
        rateLimiterValidator.setMessageValueExpression(messageValueExpression);
        rateLimiterValidator.setOnLimitMethodExpression(onLimitMethodExpression);
        rateLimiterValidator.setForValueExpression(forValueExpression);

        if (parent instanceof EditableValueHolder) {
            EditableValueHolder parentEditableValueHolder = (EditableValueHolder) parent;
            parentEditableValueHolder.addValidator(rateLimiterValidator);
            return;
        }
        if (parent instanceof ActionSource2) {
            ActionSource2 parentActionSource = (ActionSource2) parent;
            parentActionSource.addActionListener(rateLimiterValidator);
            return;
        }
        throw new TagException(this.tag, "parent must be EditableValueHolder or ActionSource2");
    }

    private int getIntegerAttributeValue(String attributeName, int defaultValue, FaceletContext context) {
        TagAttribute tagAttribute = getAttribute(attributeName);
        if (null == tagAttribute) {
            return defaultValue;
        }
        return tagAttribute.getInt(context);
    }
}
