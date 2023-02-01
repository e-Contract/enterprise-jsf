/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import java.util.HashMap;
import java.util.Map;

public enum GeolocationPositionError {
    PERMISSION_DENIED(1),
    POSITION_UNAVAILABLE(2),
    TIMEOUT(3);

    private final int code;

    private GeolocationPositionError(int code) {
        this.code = code;
    }

    private final static Map<Integer, GeolocationPositionError> CODES;

    static {
        CODES = new HashMap<>();
        for (GeolocationPositionError error : GeolocationPositionError.values()) {
            CODES.put(error.code, error);
        }
    }

    public static GeolocationPositionError fromCode(int code) {
        return CODES.get(code);
    }
}
