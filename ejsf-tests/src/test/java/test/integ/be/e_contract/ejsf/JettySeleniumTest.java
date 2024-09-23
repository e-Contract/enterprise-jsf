/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import com.sun.faces.config.ConfigureListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import jakarta.faces.webapp.FacesServlet;
import org.eclipse.jetty.cdi.CdiDecoratingListener;
import org.eclipse.jetty.cdi.CdiServletContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.weld.environment.servlet.EnhancedListener;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Message;
import org.primefaces.selenium.spi.WebDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class JettySeleniumTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettySeleniumTest.class);

    private Server server;

    private String urlPrefix;

    private WebDriver driver;

    @BeforeAll
    public static void beforeAll() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.server = new Server(0);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");

        File ejsfTaglibJar = Maven.configureResolver()
                .workOffline()
                .loadPomFromFile("pom.xml")
                .resolve("be.e-contract.enterprise-jsf:ejsf-taglib:jar:jakarta:?")
                .withoutTransitivity().asSingleFile();

        File baseDir = MavenTestingUtils.getTestResourcesDir();
        context.setBaseResource(Resource.newResource(baseDir));
        context.setExtraClasspath(ejsfTaglibJar.toURI().toString());
        this.server.setHandler(context);

        ServletHolder servletHolder = new ServletHolder(FacesServlet.class);
        context.addServlet(servletHolder, "*.xhtml");

        context.setInitParameter("jakarta.faces.PROJECT_STAGE",
                "Development");
        context.setInitParameter("com.sun.faces.forceLoadConfiguration",
                "true");
        context.addEventListener(new ConfigureListener());
        context.setInitParameter(
                CdiServletContainerInitializer.CDI_INTEGRATION_ATTRIBUTE,
                CdiDecoratingListener.MODE);
        context.addBean(new ServletContextHandler.Initializer(context,
                new CdiServletContainerInitializer()));
        context.addBean(new ServletContextHandler.Initializer(context,
                new EnhancedListener()));
        context.getMimeTypes().addMimeMapping("ttf", "application/x-font-ttf");

        this.server.start();

        ServerConnector serverConnector = (ServerConnector) this.server.getConnectors()[0];
        int port = serverConnector.getLocalPort();
        this.urlPrefix = "http://localhost:" + port + "/";

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-search-engine-choice-screen");
        this.driver = new ChromeDriver(options);

        WebDriverProvider.set(this.driver);
        MyDeploymentAdapter.setBaseUrl(this.urlPrefix);
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.driver.quit();
        this.server.stop();
    }

    @Test
    public void testRequest() throws Exception {
        this.driver.get(this.urlPrefix + "index.xhtml");

        WebElement webElement = this.driver.findElement(By.id("test"));
        assertEquals("hello world", webElement.getText());
    }

    @Test
    public void testEmailValidator() throws Exception {
        this.driver.get(this.urlPrefix + "test-email-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("hello world");

        CommandButton subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Invalid email address.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("info@e-contract.be");

        subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testUrlValidator() throws Exception {
        this.driver.get(this.urlPrefix + "test-url-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("hello world");

        CommandButton subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Custom error message.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("https://www.e-contract.be");

        subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testOtpValidator() throws Exception {
        this.driver.get(this.urlPrefix + "test-otp-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("hello world");

        CommandButton subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("This verification code is invalid.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("123456");

        subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testPlainTextValidator() throws Exception {
        this.driver.get(this.urlPrefix + "test-plain-text-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("<b>HTML text</b>");

        CommandButton subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Invalid characters.", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("plain text");

        subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }

    @Test
    public void testXMLValidator() throws Exception {
        this.driver.get(this.urlPrefix + "test-xml-validator.xhtml");

        WebElement input = this.driver.findElement(By.id("form:input"));
        input.sendKeys("no XML");

        CommandButton subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        Message message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        String messageText = message.getText();
        LOGGER.debug("message text: {}", messageText);
        assertEquals("Invalid XML (1,1).", messageText);

        input = this.driver.findElement(By.id("form:input"));
        input.clear();
        input.sendKeys("<xml/>");

        subscribeButton = PrimeSelenium.createFragment(CommandButton.class, By.id("form:submit"));
        subscribeButton.click();

        message = PrimeSelenium.createFragment(Message.class, By.id("form:message"));
        assertEquals("", message.getText());
    }
}
