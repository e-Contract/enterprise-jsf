/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf;

public class Runtime {

    private static boolean hasCommonsValidator;

    private static boolean hasOwaspHtmlSanitizer;

    private static boolean hasCaffeine;

    static {
        try {
            Class.forName("org.apache.commons.validator.routines.EmailValidator");
            hasCommonsValidator = true;
        } catch (ClassNotFoundException ex) {
            hasCommonsValidator = false;
        }

        try {
            Class.forName("org.owasp.html.HtmlPolicyBuilder");
            hasOwaspHtmlSanitizer = true;
        } catch (ClassNotFoundException ex) {
            hasOwaspHtmlSanitizer = false;
        }

        try {
            Class.forName("com.github.benmanes.caffeine.cache.Expiry");
            hasCaffeine = true;
        } catch (ClassNotFoundException ex) {
            hasCaffeine = false;
        }
    }

    public static boolean hasCommonsValidator() {
        return hasCommonsValidator;
    }

    public static boolean hasOwaspHtmlSanitizer() {
        return hasOwaspHtmlSanitizer;
    }

    public static boolean hasCaffeine() {
        return hasCaffeine;
    }
}
