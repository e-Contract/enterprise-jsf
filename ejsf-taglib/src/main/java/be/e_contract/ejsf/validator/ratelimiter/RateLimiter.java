/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiter.class);

    private static final String CACHES_ATTRIBUTE = RateLimiter.class.getName() + ".caches";

    private final int timeoutDuration;

    private final int limitRefreshPeriod;

    private final int limitForPeriod;

    public RateLimiter(int limitRefreshPeriod, int limitForPeriod, int timeoutDuration) {
        this.limitRefreshPeriod = limitRefreshPeriod;
        this.limitForPeriod = limitForPeriod;
        this.timeoutDuration = timeoutDuration;
    }

    public boolean reachedLimit(FacesContext facesContext, String identifier) {
        ExternalContext externalContext = facesContext.getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        Map<RateExpiry, Cache<String, RateInfo>> caches
                = (Map<RateExpiry, Cache<String, RateInfo>>) servletContext.getAttribute(CACHES_ATTRIBUTE);
        if (null == caches) {
            caches = new ConcurrentHashMap<>();
            servletContext.setAttribute(CACHES_ATTRIBUTE, caches);
        }

        RateExpiry rateExpiry = new RateExpiry(this.limitRefreshPeriod, this.timeoutDuration);
        Cache<String, RateInfo> cache = caches.get(rateExpiry);
        if (null == cache) {
            LOGGER.debug("creating new cache: {}", rateExpiry);
            cache = Caffeine.newBuilder()
                    .expireAfter(rateExpiry)
                    .build();
            caches.put(rateExpiry, cache);
        }
        RateInfo rateInfo = cache.get(identifier, key -> new RateInfo(this.limitForPeriod));
        boolean reachedLimit = rateInfo.reachedLimit();
        if (reachedLimit) {
            // trigger timeoutDuration
            cache.put(identifier, rateInfo);
        }
        LOGGER.debug("cache size: {}", cache.estimatedSize());
        return reachedLimit;
    }
}
