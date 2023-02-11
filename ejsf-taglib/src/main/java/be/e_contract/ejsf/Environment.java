/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Environment {

    private static boolean hasCommonsValidator;

    private static boolean hasOwaspHtmlSanitizer;

    private static boolean hasCaffeine;

    private static String version;

    private static String resourceHandlerResourceIdentifier;

    private static boolean hasPassay;

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

        try {
            try ( InputStream inputStream = Environment.class.getResourceAsStream("/META-INF/maven/be.e-contract.enterprise-jsf/ejsf-taglib/pom.properties")) {
                if (null == inputStream) {
                    // quarkus
                    version = "unknown";
                } else {
                    Properties buildProperties = new Properties();
                    buildProperties.load(inputStream);
                    version = buildProperties.getProperty("version");
                }
            }
        } catch (IOException e) {
            version = "unknown";
        }

        try {
            Class.forName("jakarta.faces.lifecycle.ClientWindowScoped");
            resourceHandlerResourceIdentifier = "/jakarta.faces.resource";
        } catch (ClassNotFoundException ex) {
            resourceHandlerResourceIdentifier = "/javax.faces.resource";
        }

        try {
            Class.forName("org.passay.PasswordValidator");
            hasPassay = true;
        } catch (ClassNotFoundException ex) {
            hasPassay = false;
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

    public static String getVersion() {
        return version;
    }

    public static String getResourceHandlerResourceIdentifier() {
        return resourceHandlerResourceIdentifier;
    }

    public static boolean hasPassay() {
        return hasPassay;
    }
}
