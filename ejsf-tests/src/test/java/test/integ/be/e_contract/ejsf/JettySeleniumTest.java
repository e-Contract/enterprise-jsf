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
import org.slf4j.bridge.SLF4JBridgeHandler;

public class JettySeleniumTest {

    private Server server;

    private String url;

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

        this.server.start();

        ServerConnector serverConnector = (ServerConnector) this.server.getConnectors()[0];
        int port = serverConnector.getLocalPort();
        this.url = "http://localhost:" + port + "/index.xhtml";

        this.driver = new ChromeDriver();
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.driver.quit();
        this.server.stop();
    }

    @Test
    public void testRequest() throws Exception {
        this.driver.get(this.url);

        WebElement webElement = this.driver.findElement(By.id("test"));
        assertEquals("hello world", webElement.getText());
    }
}
