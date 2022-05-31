package test.unit.be.e_contract.jsf.taglib.validator;

import be.e_contract.jsf.taglib.validator.ExampleServlet;
import java.net.HttpURLConnection;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExampleServletTest {

    private Server server;

    private String url;

    @BeforeEach
    public void setUp() throws Exception {
        this.server = new Server(0);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/test");
        this.server.setHandler(context);

        ServletHolder servletHolder = new ServletHolder(ExampleServlet.class);
        context.addServlet(servletHolder, "/hello");

        this.server.start();

        ServerConnector serverConnector = (ServerConnector) this.server.getConnectors()[0];
        int port = serverConnector.getLocalPort();
        this.url = "http://localhost:" + port + "/test/hello";
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
                assertEquals("hello world", body);
            }
        }
    }
}
