/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.platform;

import java.util.HashMap;
import java.util.Map;

public enum Platform {

    WINDOWS("windows"),
    MACOS("macos"),
    LINUX("linux"),
    FREEBSD("freebsd"),
    UNKNOWN("unknown");

    private final String name;

    private Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private static final Map<String, Platform> PLATFORMS;

    static {
        PLATFORMS = new HashMap<>();
        for (Platform platform : Platform.values()) {
            PLATFORMS.put(platform.getName(), platform);
        }
    }

    public static final Platform toPlatform(String name) {
        Platform platform = PLATFORMS.get(name);
        if (null != platform) {
            return platform;
        }
        return Platform.UNKNOWN;
    }
}
