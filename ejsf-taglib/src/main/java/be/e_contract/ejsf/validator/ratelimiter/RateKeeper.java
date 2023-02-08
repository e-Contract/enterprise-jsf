/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 * Rate Gate Keeper.
 *
 * @author Frank Cornelis
 */
public class RateKeeper {

    private static final String CACHE_ATTRIBUTE = RateKeeper.class.getName() + ".cache";

    private RateKeeper() {
        super();
    }

    public static boolean reachedLimit(FacesContext facesContext, String identifier) {
        ExternalContext externalContext = facesContext.getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        LoadingCache<String, RateInfo> cache = (LoadingCache<String, RateInfo>) servletContext.getAttribute(CACHE_ATTRIBUTE);
        if (null == cache) {
            cache = Caffeine.newBuilder()
                    .expireAfter(new RateExpiry())
                    .build(key -> new RateInfo());
            servletContext.setAttribute(CACHE_ATTRIBUTE, cache);
        }
        RateInfo rateInfo = cache.get(identifier);
        return rateInfo.reachedLimit();
    }
}
