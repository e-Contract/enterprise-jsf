/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import com.github.benmanes.caffeine.cache.Expiry;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateExpiry implements Expiry<String, RateInfo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateExpiry.class);

    @Override
    public long expireAfterCreate(String key, RateInfo value, long currentTime) {
        return TimeUnit.SECONDS.toNanos(10);
    }

    @Override
    public long expireAfterUpdate(String key, RateInfo value, long currentTime, long currentDuration) {
        LOGGER.debug("expire after update: {}", currentDuration);
        return currentDuration;
    }

    @Override
    public long expireAfterRead(String key, RateInfo value, long currentTime, long currentDuration) {
        LOGGER.debug("expire after read: {}", currentDuration);
        return currentDuration;
    }
}
