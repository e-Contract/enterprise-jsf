package be.e_contract.jsf.security;

import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SecurityServletContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.debug("contextInitialized");
        AuthConfigFactory authConfigFactory = AuthConfigFactory.getFactory();
        AuthConfigProvider authConfigProvider = new DemoAuthConfigProvider();
        ServletContext servletContext = sce.getServletContext();
        String appContext = servletContext.getVirtualServerName() + " " + servletContext.getContextPath();
        LOGGER.debug("app context: {}", appContext);
        authConfigFactory.registerConfigProvider(authConfigProvider, "HttpServlet", appContext, "JASPIC Demo");
    }
}
