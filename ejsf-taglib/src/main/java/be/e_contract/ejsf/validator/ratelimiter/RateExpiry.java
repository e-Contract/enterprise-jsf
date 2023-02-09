/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import com.github.benmanes.caffeine.cache.Expiry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateExpiry implements Expiry<String, RateInfo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateExpiry.class);

    private final int limitForPeriod;

    private final int timeoutDuration;

    public RateExpiry(int limitForPeriod, int timeoutDuration) {
        this.limitForPeriod = limitForPeriod;
        this.timeoutDuration = timeoutDuration;
    }

    @Override
    public long expireAfterCreate(String key, RateInfo value, long currentTime) {
        LOGGER.debug("expire after create");
        return TimeUnit.SECONDS.toNanos(this.limitForPeriod);
    }

    @Override
    public long expireAfterUpdate(String key, RateInfo value, long currentTime, long currentDuration) {
        LOGGER.debug("expire after update: {}", currentDuration);
        return TimeUnit.SECONDS.toNanos(this.timeoutDuration);
    }

    @Override
    public long expireAfterRead(String key, RateInfo value, long currentTime, long currentDuration) {
        LOGGER.debug("expire after read: {}", currentDuration);
        return currentDuration;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.limitForPeriod)
                .append(this.timeoutDuration)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof RateExpiry)) {
            return false;
        }
        RateExpiry other = (RateExpiry) obj;
        return new EqualsBuilder()
                .append(this.limitForPeriod, other.limitForPeriod)
                .append(this.timeoutDuration, other.timeoutDuration)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append(this.limitForPeriod)
                .append(this.timeoutDuration)
                .build();
    }
}
