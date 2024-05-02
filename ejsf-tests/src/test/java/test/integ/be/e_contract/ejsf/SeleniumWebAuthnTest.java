/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v124.webauthn.WebAuthn;
import org.openqa.selenium.devtools.v124.webauthn.model.AuthenticatorId;
import org.openqa.selenium.devtools.v124.webauthn.model.AuthenticatorProtocol;
import org.openqa.selenium.devtools.v124.webauthn.model.AuthenticatorTransport;
import org.openqa.selenium.devtools.v124.webauthn.model.Ctap2Version;
import org.openqa.selenium.devtools.v124.webauthn.model.VirtualAuthenticatorOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumWebAuthnTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumWebAuthnTest.class);

    @BeforeAll
    public static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    public void testWebAuthn() throws Exception {
        LOGGER.debug("WebAuthn integration test");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-ssl-errors=yes", "--ignore-certificate-errors", "--remote-allow-origins=*");
        ChromeDriver driver = new ChromeDriver(options);

        driver.get("https://localhost/ejsf-demo/webauthn.xhtml");

        DevTools devTools = driver.getDevTools();
        devTools.createSession();
        devTools.send(WebAuthn.enable(Optional.of(true)));
        VirtualAuthenticatorOptions virtualAuthenticatorOptions
                = new VirtualAuthenticatorOptions(AuthenticatorProtocol.CTAP2,
                        Optional.of(Ctap2Version.CTAP2_1),
                        AuthenticatorTransport.NFC,
                        Optional.of(true),
                        Optional.of(true),
                        Optional.of(true),
                        Optional.of(true),
                        Optional.of(true),
                        Optional.of(true),
                        Optional.of(true),
                        Optional.of(true),
                        Optional.of(true),
                        Optional.of(true));
        AuthenticatorId authenticatorId = devTools.send(WebAuthn.addVirtualAuthenticator(virtualAuthenticatorOptions));
        LOGGER.debug("authenticator id: {}", authenticatorId.toString());

        String username = UUID.randomUUID().toString();
        WebElement registrationUsernameElement = driver.findElement(By.id("registrationForm:username"));
        registrationUsernameElement.sendKeys(username);

        WebElement authenticationUsernameElement = driver.findElement(By.id("authenticationForm:username"));
        authenticationUsernameElement.sendKeys(username);

        WebElement registerButton = driver.findElement(By.id("registrationForm:registerButton"));
        registerButton.click();

        Thread.sleep(1000 * 20);

        WebElement authenticateButton = driver.findElement(By.id("authenticationForm:authenticateButton"));
        authenticateButton.click();

        Thread.sleep(1000 * 5);

        devTools.send(WebAuthn.removeVirtualAuthenticator(authenticatorId));
        devTools.send(WebAuthn.disable());

        driver.close();
    }
}
