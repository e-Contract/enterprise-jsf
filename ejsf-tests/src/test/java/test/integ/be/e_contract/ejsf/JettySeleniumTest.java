/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import be.e_contract.ejsf.component.clocksync.ClockSyncServlet;
import be.e_contract.ejsf.component.geolocation.GeolocationPositionError;
import test.integ.be.e_contract.ejsf.cdi.StopTestEvent;
import test.integ.be.e_contract.ejsf.cdi.StartTestEvent;
import com.sun.faces.config.ConfigureListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import java.io.File;
import jakarta.faces.webapp.FacesServlet;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.RSAKeyGenParameterSpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.eclipse.jetty.cdi.CdiDecoratingListener;
import org.eclipse.jetty.cdi.CdiServletContainerInitializer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.weld.environment.servlet.EnhancedListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v129.webauthn.WebAuthn;
import org.openqa.selenium.devtools.v129.webauthn.model.AuthenticatorId;
import org.openqa.selenium.devtools.v129.webauthn.model.AuthenticatorProtocol;
import org.openqa.selenium.devtools.v129.webauthn.model.AuthenticatorTransport;
import org.openqa.selenium.devtools.v129.webauthn.model.Ctap2Version;
import org.openqa.selenium.devtools.v129.webauthn.model.VirtualAuthenticatorOptions;
import org.openqa.selenium.devtools.v129.emulation.Emulation;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Button;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Message;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;
import org.primefaces.selenium.spi.WebDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class JettySeleniumTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettySeleniumTest.class);

    private static Server server;

    private static String urlPrefix;

    private ChromeDriver driver;

    @BeforeAll
    public static void beforeAll() throws Exception {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        WebDriverManager.chromedriver().setup();

        JettySeleniumTest.server = new Server(0);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");

        File ejsfTaglibJar = Maven.configureResolver()
                .workOffline()
                .loadPomFromFile("pom.xml")
                .resolve("be.e-contract.enterprise-jsf:ejsf-taglib:jar:jakarta:?")
                .withoutTransitivity().asSingleFile();
        // Per default Jetty does not scan JARs for annotations.
        // This causes our JSF components not to get registered.
        // Hence we force our JAR upon Jetty.
        context.getMetaData().addContainerResource(Resource.newResource(ejsfTaglibJar));

        File baseDir = MavenTestingUtils.getTestResourcesDir();
        context.setBaseResource(Resource.newResource(baseDir));
        JettySeleniumTest.server.setHandler(context);

        ServletHolder facesServletHolder = new ServletHolder(FacesServlet.class);
        context.addServlet(facesServletHolder, "*.xhtml");

        ServletHolder clockServletHolder = new ServletHolder(ClockSyncServlet.class);
        context.addServlet(clockServletHolder, "/clock");

        context.setInitParameter("jakarta.faces.PROJECT_STAGE",
                "Development");
        context.setInitParameter("com.sun.faces.forceLoadConfiguration",
                "true");
        context.addEventListener(new ConfigureListener());
        context.setInitParameter(
                CdiServletContainerInitializer.CDI_INTEGRATION_ATTRIBUTE,
                CdiDecoratingListener.MODE);
        context.addServletContainerInitializer(new CdiServletContainerInitializer());
        context.addServletContainerInitializer(new EnhancedListener());
        context.getMimeTypes().addMimeMapping("ttf", "application/x-font-ttf");

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        KeyStore keyStore = createKeyStore();
        sslContextFactory.setKeyStore(keyStore);
        sslContextFactory.setKeyStorePassword("secret");
        ServerConnector sslServerConnector = new ServerConnector(JettySeleniumTest.server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        JettySeleniumTest.server.addConnector(sslServerConnector);

        JettySeleniumTest.server.start();

        int sslPort = sslServerConnector.getLocalPort();
        LOGGER.debug("Jetty SSL port: {}", sslPort);
        JettySeleniumTest.urlPrefix = "https://localhost:" + sslPort + "/";
    }

    private static KeyStore createKeyStore() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        keyPairGenerator.initialize(new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4),
                secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        X500Name subjectName = new X500Name("CN=localhost");
        BigInteger serial = new BigInteger(128, secureRandom);
        LocalDateTime notBefore = LocalDateTime.now();
        LocalDateTime notAfter = notBefore.plusYears(1);
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        X509v3CertificateBuilder x509v3CertificateBuilder = new X509v3CertificateBuilder(subjectName, serial,
                Date.from(notBefore.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(notAfter.atZone(ZoneId.systemDefault()).toInstant()), subjectName, publicKeyInfo);
        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
                .find("SHA256withRSA");
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
        AsymmetricKeyParameter asymmetricKeyParameter = PrivateKeyFactory
                .createKey(privateKey.getEncoded());
        ContentSigner contentSigner = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(asymmetricKeyParameter);
        X509CertificateHolder x509CertificateHolder = x509v3CertificateBuilder.build(contentSigner);

        byte[] encodedCertificate = x509CertificateHolder.getEncoded();

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory
                .generateCertificate(new ByteArrayInputStream(encodedCertificate));

        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(null, "secret".toCharArray());
        keyStore.setKeyEntry("alias", privateKey, "secret".toCharArray(), new Certificate[]{certificate});
        return keyStore;
    }

    @BeforeEach
    public void setUp() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-ssl-errors=yes", "--ignore-certificate-errors", "--disable-search-engine-choice-screen");
        this.driver = new ChromeDriver(options);

        WebDriverProvider.set(this.driver);
        MyDeploymentAdapter.setBaseUrl(JettySeleniumTest.urlPrefix);

        fireEvent(new StartTestEvent());
    }

    @AfterEach
    public void tearDown() throws Exception {
        fireEvent(new StopTestEvent());

        this.driver.quit();
    }

    private void fireEvent(Object eventObject) {
        CDI cdi = CDI.current();
        BeanManager beanManager = cdi.getBeanManager();
        Event event = beanManager.getEvent();
        event.fire(eventObject);
    }

    @AfterAll
    public static void afterAll() throws Exception {
        JettySeleniumTest.server.stop();
    }

    @Test
    public void testRequest() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "index.xhtml");

        WebElement webElement = this.driver.findElement(By.id("test"));
        assertEquals("hello world", webElement.getText());
    }

    @Test
    public void testEmailValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-email-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("hello world");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Invalid email address.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("info@e-contract.be");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testUrlValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-url-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("hello world");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Custom error message.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("https://www.e-contract.be");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testOtpValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-otp-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("hello world");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("This verification code is invalid.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("123456");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testPlainTextValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-plain-text-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("<b>HTML text</b>");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Invalid characters.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("plain text");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testXMLValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-xml-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("no XML");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Invalid XML (1,1).", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("<xml/>");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testGeolocation() throws Exception {
        DevTools devTools = this.driver.getDevTools();
        devTools.createSession();

        devTools.send(Emulation.setGeolocationOverride(Optional.of(1), Optional.of(2), Optional.of(3)));

        this.driver.get(JettySeleniumTest.urlPrefix + "test-geolocation.xhtml");

        Button button = PrimeSelenium.createFragment(Button.class, By.id("button"));
        button.click();

        while (true) {
            WebElement latitude = this.driver.findElement(By.id("latitude"));
            if (!latitude.getText().isEmpty()) {
                break;
            }
            Thread.sleep(500);
        }

        WebElement latitude = this.driver.findElement(By.id("latitude"));
        assertEquals("1.0", latitude.getText());

        WebElement longitude = this.driver.findElement(By.id("longitude"));
        assertEquals("2.0", longitude.getText());

        WebElement accuracy = this.driver.findElement(By.id("accuracy"));
        assertEquals("3.0 m", accuracy.getText());

        runOnBean(GeolocationController.class, (GeolocationController geolocationController) -> {
            assertEquals(1.0, geolocationController.getLatitude());
            assertEquals(2.0, geolocationController.getLongitude());
            assertEquals(3.0, geolocationController.getAccuracy());

            geolocationController.reset();
        });

        devTools.send(Emulation.setGeolocationOverride(Optional.empty(), Optional.empty(), Optional.empty()));

        this.driver.get(JettySeleniumTest.urlPrefix + "test-geolocation.xhtml");
        button = PrimeSelenium.createFragment(Button.class, By.id("button"));
        button.click();

        runOnBean(GeolocationController.class, (GeolocationController geolocationController) -> {
            while (null == geolocationController.getError()) {
                Thread.sleep(500);
            }
            assertEquals(GeolocationPositionError.POSITION_UNAVAILABLE, geolocationController.getError());
        });

        devTools.send(Emulation.clearGeolocationOverride());
    }

    @Test
    public void testAgeValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-age-validator.xhtml");

        DatePicker input = PrimeSelenium.createFragment(DatePicker.class, By.id("form:input"));
        input.setDate(LocalDateTime.now());

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("The minimum age is 18 years.", messageText);

        input = PrimeSelenium.createFragment(DatePicker.class, By.id("form:input"));
        input.setDate(LocalDateTime.now().minusYears(18).minusDays(1));

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testOidValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-oid-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("no OID");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Not an OID.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("1.2.3.4");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testCronValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-cron-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("no UNIX CRON");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Not a valid cron expression.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("0 0 * * *");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testWebAuthn() throws Exception {
        DevTools devTools = this.driver.getDevTools();
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
        try {
            this.driver.get(JettySeleniumTest.urlPrefix + "test-webauthn.xhtml");

            WebElement registerInput = this.driver.findElement(By.id("registrationForm:username"));
            registerInput.sendKeys("username");

            CommandButton registerButton = PrimeSelenium.createFragment(CommandButton.class, By.id("registrationForm:registerButton"));
            registerButton.click();

            runOnBean(WebAuthnController.class, (WebAuthnController webAuthnController) -> {
                while (true) {
                    if (webAuthnController.isRegistered() && webAuthnController.isRegistered2()) {
                        break;
                    }
                    Thread.sleep(1000);
                }

                WebElement authenticateInput = this.driver.findElement(By.id("authenticationForm:username"));
                authenticateInput.sendKeys("username");

                CommandButton authenticateButton = PrimeSelenium.createFragment(CommandButton.class, By.id("authenticationForm:authenticateButton"));
                authenticateButton.click();

                while (true) {
                    if (webAuthnController.isAuthenticated() && webAuthnController.isAuthenticated2()) {
                        break;
                    }
                    Thread.sleep(1000);
                }
            });
        } finally {
            devTools.send(WebAuthn.removeVirtualAuthenticator(authenticatorId));
            devTools.send(WebAuthn.disable());
        }
    }

    @Test
    public void testInputPeriod() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-input-period.xhtml");

        SelectOneMenu minutes = PrimeSelenium.createFragment(SelectOneMenu.class, By.id("form:input:minutesRange"));
        minutes.select("1");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        runOnBean(InputPeriodController.class, (InputPeriodController inputPeriodController) -> {
            int value = inputPeriodController.getValue();
            assertEquals(60, value);
        });
    }

    @Test
    public void testClockSync() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-clock-sync.xhtml");

        runOnBean(ClockSyncController.class, (ClockSyncController clockSyncController) -> {
            while (true) {
                if (clockSyncController.isSynced()) {
                    break;
                }
                Thread.sleep(500);
            }
            LOGGER.debug("best round trip: {} ms", clockSyncController.getBestRoundTripDelay());
            LOGGER.debug("delta T: {} ms", clockSyncController.getDeltaT());
            // next is due to the foobar clock sync
            assertTrue(clockSyncController.isError());
        });

        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) this.driver;
        Object result = javascriptExecutor.executeScript("return PF('clockSync').isSynchronized();");
        LOGGER.debug("result: {}", result);
        LOGGER.debug("result type: {}", result.getClass().getName());
        assertTrue((Boolean) result);

        Object errorResult = javascriptExecutor.executeScript("return PF('foobarClockSync').isSynchronized();");
        assertFalse((Boolean) errorResult);
    }

    @Test
    public void testInetAddressValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-inet-address-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("no internet address");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Invalid internet address.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("127.0.0.1");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testPinValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-pin-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("no PIN");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Not a PIN.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("123456");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testRateLimiter() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-rate-limiter.xhtml");

        WebElement usernameInput = this.driver.findElement(By.id("form:username"));
        usernameInput.sendKeys("username");

        WebElement passwordInput = this.driver.findElement(By.id("form:password"));
        passwordInput.sendKeys("password");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());

        for (int i = 0; i < 5; i++) {
            runOnBean(RateLimiterController.class, (RateLimiterController rateLimiterController) -> {
                assertNull(rateLimiterController.getUsername());
            });

            submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
            submitButton.click();
        }

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("Please try again later.", message.getText());

        runOnBean(RateLimiterController.class, (RateLimiterController rateLimiterController) -> {
            assertEquals("username", rateLimiterController.getUsername());
        });
    }

    @Test
    public void testRateLimiterAction() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-rate-limiter-action.xhtml");

        WebElement usernameInput = this.driver.findElement(By.id("form:username"));
        usernameInput.sendKeys("username2");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        for (int i = 0; i < 5; i++) {
            runOnBean(RateLimiterController.class, (RateLimiterController rateLimiterController) -> {
                assertNull(rateLimiterController.getUsername());
            });

            submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
            submitButton.click();
        }

        runOnBean(RateLimiterController.class, (RateLimiterController rateLimiterController) -> {
            assertEquals("username2", rateLimiterController.getUsername());
            assertEquals(5, rateLimiterController.getActionCount());
        });

        Messages messages = PrimeSelenium.createFragment(Messages.class, By.id("messages"));
        LOGGER.debug("messages: {}", messages.getAllSummaries());
        assertTrue(messages.getAllSummaries().contains("Reached limit."));
    }

    @Test
    public void testTrimValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-trim-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys(" white spaces ");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Leading/trailing white spaces.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("no white spaces");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testPasswordValidator() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-password-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("12345678901");

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Must be at least 12 characters.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("123456789012");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("Should contain at least one lower case character.", message.getText());

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("a23456789012");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("Should contain at least one upper case character.", message.getText());

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("aA3456789012");

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testPerformanceNavigation() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-performance-navigation.xhtml");

        runOnBean(PerformanceNavigationController.class, (PerformanceNavigationController performanceNavigationController) -> {
            while (!performanceNavigationController.isDone()) {
                Thread.sleep(500);
            }
            performanceNavigationController.reset();
        });

        DevTools devTools = this.driver.getDevTools();
        devTools.createSession();
        devTools.send(Emulation.setCPUThrottlingRate(90));
        this.driver.get(JettySeleniumTest.urlPrefix + "test-performance-navigation.xhtml");

        runOnBean(PerformanceNavigationController.class, (PerformanceNavigationController performanceNavigationController) -> {
            while (!performanceNavigationController.isDone()) {
                Thread.sleep(500);
            }
            performanceNavigationController.reset();
        });
    }

    @Test
    public void testAddMessage() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-add-message.xhtml");

        String paramValue = UUID.randomUUID().toString();
        runOnBean(AddMessageController.class, (AddMessageController addMessageController) -> {
            addMessageController.setCallbackParam("callbackParam", paramValue);
        });

        CommandButton submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        submitButton.click();

        Messages messages = PrimeSelenium.createFragment(Messages.class, By.id("messages"));
        String messageText = messages.getText();
        LOGGER.debug("message text: {}", messageText);

        assertEquals("Warning message: " + paramValue, messageText);

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form2:submit"));
        submitButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form2:message"));
        // PrimeFaces mixes detail with summary or what?
        LOGGER.debug("error detail: {}", message.getDetailError().getText());
        assertEquals("Warning message: " + paramValue, message.getText());
        InputText inputText = PrimeSelenium.createFragment(InputText.class, By.id("form2:input"));
        String inputClasses = inputText.getInput().getAttribute("class");
        LOGGER.debug("input classes: {}", inputClasses);
        assertTrue(inputClasses.contains("ui-state-error"));

        submitButton = PrimeSelenium.createFragment(CommandButton.class, By.id("paramsForm:submit"));
        submitButton.click();

        messages = PrimeSelenium.createFragment(Messages.class, By.id("messages"));
        messageText = messages.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("value 0 true", messageText);
    }

    @Test
    public void testBrowser() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-browser.xhtml");

        WebElement webElement = this.driver.findElement(By.id("result"));
        assertEquals("chrome", webElement.getText());

        DevTools devTools = this.driver.getDevTools();
        devTools.createSession();

        // Edge
        devTools.send(Emulation.setUserAgentOverride("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36 Edg/129.0.2792.65", Optional.empty(), Optional.empty(), Optional.empty()));

        this.driver.get(JettySeleniumTest.urlPrefix + "test-browser.xhtml");

        webElement = this.driver.findElement(By.id("result"));
        assertEquals("edge", webElement.getText());

        // Firefox
        devTools.send(Emulation.setUserAgentOverride("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:130.0) Gecko/20100101 Firefox/130.0", Optional.empty(), Optional.empty(), Optional.empty()));

        this.driver.get(JettySeleniumTest.urlPrefix + "test-browser.xhtml");

        webElement = this.driver.findElement(By.id("result"));
        assertEquals("firefox", webElement.getText());

        // Safari
        devTools.send(Emulation.setUserAgentOverride("Mozilla/5.0 (Macintosh; Intel Mac OS X 14_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.0 Safari/605.1.15", Optional.empty(), Optional.empty(), Optional.empty()));

        this.driver.get(JettySeleniumTest.urlPrefix + "test-browser.xhtml");

        webElement = this.driver.findElement(By.id("result"));
        assertEquals("safari", webElement.getText());

        // Unknown
        devTools.send(Emulation.setUserAgentOverride("foobar", Optional.empty(), Optional.empty(), Optional.empty()));

        this.driver.get(JettySeleniumTest.urlPrefix + "test-browser.xhtml");

        webElement = this.driver.findElement(By.id("result"));
        assertEquals("unknown", webElement.getText());
    }

    @Test
    public void testPlatforms() throws Exception {
        DevTools devTools = this.driver.getDevTools();
        devTools.createSession();

        // Linux
        devTools.send(Emulation.setUserAgentOverride("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:130.0) Gecko/20100101 Firefox/130.0", Optional.empty(), Optional.empty(), Optional.empty()));
        this.driver.get(JettySeleniumTest.urlPrefix + "test-platforms.xhtml");

        WebElement webElement = this.driver.findElement(By.id("result"));
        assertEquals("linux", webElement.getText());

        // macOS
        devTools.send(Emulation.setUserAgentOverride("Mozilla/5.0 (Macintosh; Intel Mac OS X 14.7; rv:130.0) Gecko/20100101 Firefox/130.0", Optional.empty(), Optional.empty(), Optional.empty()));
        this.driver.get(JettySeleniumTest.urlPrefix + "test-platforms.xhtml");

        webElement = this.driver.findElement(By.id("result"));
        assertEquals("macos", webElement.getText());

        // Windows
        devTools.send(Emulation.setUserAgentOverride("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:130.0) Gecko/20100101 Firefox/130.0", Optional.empty(), Optional.empty(), Optional.empty()));
        this.driver.get(JettySeleniumTest.urlPrefix + "test-platforms.xhtml");

        webElement = this.driver.findElement(By.id("result"));
        assertEquals("windows", webElement.getText());

        // FreeBSD
        devTools.send(Emulation.setUserAgentOverride("Mozilla/5.0 (X11; FreeBSD x86_64; rv:125.0) Gecko/20100101 Firefox/125.0", Optional.empty(), Optional.empty(), Optional.empty()));
        this.driver.get(JettySeleniumTest.urlPrefix + "test-platforms.xhtml");

        webElement = this.driver.findElement(By.id("result"));
        assertEquals("freebsd", webElement.getText());

        // unknown
        devTools.send(Emulation.setUserAgentOverride("foobar", Optional.empty(), Optional.empty(), Optional.empty()));
        this.driver.get(JettySeleniumTest.urlPrefix + "test-platforms.xhtml");

        webElement = this.driver.findElement(By.id("result"));
        assertTrue(webElement.getText().contains("linux"));
        assertTrue(webElement.getText().contains("macos"));
        assertTrue(webElement.getText().contains("windows"));
        assertTrue(webElement.getText().contains("freebsd"));
    }

    @Test
    public void testLink() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-link.xhtml");

        WebElement webElement = this.driver.findElement(By.cssSelector("link[rel='canonical']"));
        String href = webElement.getAttribute("href");
        assertEquals("https://www.e-contract.be/", href);
    }

    @Test
    public void testLinkedData() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-linked-data.xhtml");

        WebElement webElement = this.driver.findElement(By.cssSelector("script[type='application/ld+json']"));
        String jsonLd = webElement.getAttribute("innerHTML");
        LOGGER.debug("JSON-LD: {}", jsonLd);
        assertTrue(StringUtils.isNotEmpty(jsonLd));
    }

    @Test
    public void testRobots() throws Exception {
        this.driver.get(JettySeleniumTest.urlPrefix + "test-robots.xhtml");

        WebElement webElement = this.driver.findElement(By.cssSelector("meta[name='robots']"));
        String content = webElement.getAttribute("content");
        assertEquals("noindex, nofollow", content);

        runOnBean(RobotsController.class, (RobotsController robotsController) -> {
            robotsController.setFollow(true);
        });

        this.driver.get(JettySeleniumTest.urlPrefix + "test-robots.xhtml");

        webElement = this.driver.findElement(By.cssSelector("meta[name='robots']"));
        content = webElement.getAttribute("content");
        assertEquals("noindex, follow", content);

        runOnBean(RobotsController.class, (RobotsController robotsController) -> {
            robotsController.setIndex(true);
        });

        this.driver.get(JettySeleniumTest.urlPrefix + "test-robots.xhtml");

        webElement = this.driver.findElement(By.cssSelector("meta[name='robots']"));
        content = webElement.getAttribute("content");
        assertEquals("index, follow", content);
    }

    @FunctionalInterface
    interface ExceptionConsumer<T> {

        void accept(T instance) throws Exception;
    }

    private <T> void runOnBean(Class<T> beanClass, ExceptionConsumer<T> consumer) throws Exception {
        CDI cdi = CDI.current();
        BeanManager beanManager = cdi.getBeanManager();
        Bean<T> bean = (Bean<T>) beanManager.getBeans(beanClass).iterator().next();
        CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
        try {
            T instance = (T) beanManager.getReference(bean, beanClass, creationalContext);
            consumer.accept(instance);
        } finally {
            creationalContext.release();
        }
    }
}
