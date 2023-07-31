package test.unit.be.e_contract.jsf.taglib.validator;

import com.sun.faces.config.ConfigureListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import javax.faces.webapp.FacesServlet;
import org.eclipse.jetty.cdi.CdiDecoratingListener;
import org.eclipse.jetty.cdi.CdiServletContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
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

public class JettySeleniumTest {

    private Server server;

    private String url;

    private WebDriver driver;

    @BeforeAll
    public static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.server = new Server(0);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");

        File baseDir = MavenTestingUtils.getTestResourcesDir();
        context.setBaseResource(Resource.newResource(baseDir));
        this.server.setHandler(context);

        ServletHolder servletHolder = new ServletHolder(FacesServlet.class);
        context.addServlet(servletHolder, "*.xhtml");

        context.setInitParameter("javax.faces.PROJECT_STAGE",
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
