package test.unit.be.e_contract.jsf.taglib.validator;

import java.io.File;
import java.net.HttpURLConnection;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
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

    @BeforeEach
    public void setUp() throws Exception {
        this.server = new Server(0);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/test");
        File baseDir = MavenTestingUtils.getTestResourcesDir();
        context.setBaseResource(Resource.newResource(baseDir));
        this.server.setHandler(context);

        this.server.start();

        ServerConnector serverConnector = (ServerConnector) this.server.getConnectors()[0];
        int port = serverConnector.getLocalPort();
        this.baseUrl = "http://localhost:" + port + "/test";
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.server.stop();
    }

    @Test
    public void testRequest() throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        try ( CloseableHttpClient httpClient = httpClientBuilder.build()) {
            HttpGet httpGet = new HttpGet(this.baseUrl);
            try ( CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                int statusCode = httpResponse.getCode();
                assertEquals(HttpURLConnection.HTTP_OK, statusCode);
                String body = EntityUtils.toString(httpResponse.getEntity());
                LOGGER.debug("body: {}", body);
            }
        }
    }
}
