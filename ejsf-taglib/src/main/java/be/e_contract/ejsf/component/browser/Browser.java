/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.browser;

import java.util.HashMap;
import java.util.Map;

public enum Browser {

    CHROME("chrome"),
    EDGE("edge"),
    FIREFOX("firefox"),
    SAFARI("safari"),
    UNKNOWN("unknown");

    private final String name;

    private Browser(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private static final Map<String, Browser> BROWSERS;

    static {
        BROWSERS = new HashMap<>();
        for (Browser browser : Browser.values()) {
            BROWSERS.put(browser.getName(), browser);
        }
    }

    public static Browser toBrowser(String name) {
        Browser browser = BROWSERS.get(name);
        if (null != browser) {
            return browser;
        }
        return Browser.UNKNOWN;
    }
}
