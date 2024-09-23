/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import org.primefaces.selenium.spi.DeploymentAdapter;

public class MyDeploymentAdapter implements DeploymentAdapter {

    private static String baseUrl;

    public static void setBaseUrl(String baseUrl) {
        MyDeploymentAdapter.baseUrl = baseUrl;
    }

    @Override
    public void startup() throws Exception {
        // empty
    }

    @Override
    public String getBaseUrl() {
        return MyDeploymentAdapter.baseUrl;
    }

    @Override
    public void shutdown() throws Exception {
        // empty
    }
}
