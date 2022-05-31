package test.unit.be.e_contract.jsf.taglib.validator;

import be.e_contract.jsf.taglib.validator.ExampleServlet;
import java.io.File;
import java.net.HttpURLConnection;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleJSFTest {

    private final Logger LOGGER = LoggerFactory.getLogger(ExampleJSFTest.class);

    private Server server;

    private String baseUrl;

    private String servletUrl;

    @BeforeEach
    public void setUp() throws Exception {
        this.server = new Server(0);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/test");

        File baseDir = MavenTestingUtils.getTestResourcesDir();
        context.setBaseResource(Resource.newResource(baseDir));
        this.server.setHandler(context);

        ServletHolder servletHolder = new ServletHolder(ExampleServlet.class);
        servletHolder.setInitParameter("pathInfoOnly", "true");
        context.addServlet(servletHolder, "/hello");

        this.server.start();
        ServerConnector serverConnector = (ServerConnector) this.server.getConnectors()[0];
        int freePort = serverConnector.getLocalPort();
        this.baseUrl = "http://localhost:" + freePort + "/test";
        this.servletUrl = this.baseUrl + "/hello";
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.server.stop();
    }

    @Test
    public void testRequest() throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        try ( CloseableHttpClient httpClient = httpClientBuilder.build()) {
            HttpGet httpGet = new HttpGet(this.servletUrl);
            try ( CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                assertEquals(HttpURLConnection.HTTP_OK, statusCode);
                String body = EntityUtils.toString(httpResponse.getEntity());
                assertEquals("hello world", body);
            }
            httpGet = new HttpGet(this.baseUrl);
            try ( CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                assertEquals(HttpURLConnection.HTTP_OK, statusCode);
                String body = EntityUtils.toString(httpResponse.getEntity());
                LOGGER.debug("body: {}", body);
            }
        }
    }
}
