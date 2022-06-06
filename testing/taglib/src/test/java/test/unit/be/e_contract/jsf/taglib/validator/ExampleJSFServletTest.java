package test.unit.be.e_contract.jsf.taglib.validator;

import com.sun.faces.config.ConfigureListener;
import java.io.File;
import java.net.HttpURLConnection;
import javax.faces.webapp.FacesServlet;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExampleJSFServletTest {

    private Server server;

    private String url;

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

        context.setInitParameter("javax.faces.PROJECT_STAGE", "Development");
        context.setInitParameter("com.sun.faces.forceLoadConfiguration", "true");
        context.addEventListener(new ConfigureListener());
        context.setInitParameter(CdiServletContainerInitializer.CDI_INTEGRATION_ATTRIBUTE, CdiDecoratingListener.MODE);
        context.addBean(new ServletContextHandler.Initializer(context, new CdiServletContainerInitializer()));
        context.addBean(new ServletContextHandler.Initializer(context, new EnhancedListener()));

        this.server.start();

        ServerConnector serverConnector = (ServerConnector) this.server.getConnectors()[0];
        int port = serverConnector.getLocalPort();
        this.url = "http://localhost:" + port + "/index.xhtml";
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.server.stop();
    }

    @Test
    public void testRequest() throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        try ( CloseableHttpClient httpClient = httpClientBuilder.build()) {
            HttpGet httpGet = new HttpGet(this.url);
            try ( CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                int statusCode = httpResponse.getCode();
                assertEquals(HttpURLConnection.HTTP_OK, statusCode);
                String body = EntityUtils.toString(httpResponse.getEntity());
                assertTrue(body.contains("hello world"));
            }
        }
    }
}
