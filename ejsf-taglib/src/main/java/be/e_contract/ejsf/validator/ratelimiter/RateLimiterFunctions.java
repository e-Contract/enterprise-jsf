/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;

public class RateLimiterFunctions {

    public static String getRateLimiterSessionIdentifier(String noSessionIdentifier) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
        HttpSession httpSession = httpServletRequest.getSession(false);
        String sessionId;
        if (null != httpSession) {
            sessionId = httpSession.getId();
        } else {
            sessionId = noSessionIdentifier;
        }
        return DigestUtils.sha1Hex(sessionId);
    }
}
