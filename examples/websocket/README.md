# WebSocket Example Web Application

This example demonstrates how to use websockets via JSF or directly via the JSR 356 Java API for WebSockets.

## Running the example

To run the example on Jetty, use:
```
mvn clean jetty:run -Pmojarra
```

To deploy on a local running JBoss EAP/WildFly, use:
```
mvn clean wildfly:deploy
```

## Apache Web Server Proxy

For Apache Web Server version 2.4.47 or higher, you can use the following configuration:
```
<Location "/websocket-demo">
    ProxyPass http://localhost:8080/websocket-demo upgrade=websocket
    ProxyPassReverse http://localhost:8080/websocket-demo
    ProxyPreserveHost On
</Location>
```

For older versions of the Apache Web Server, you need to enable the `proxy_wstunnel_module` and `rewrite_module` modules.
Next you can use the following configuration:
```
<Location "/websocket-demo">
    ProxyPass http://localhost:8080/websocket-demo
    ProxyPassReverse http://localhost:8080/websocket-demo
    ProxyPreserveHost On

    RewriteEngine on
    RewriteCond %{HTTP:UPGRADE} ^WebSocket$ [NC]
    RewriteCond %{HTTP:CONNECTION} Upgrade$ [NC]
    RewriteRule /javax.faces.push/(.*) ws://localhost:8080/websocket-demo/javax.faces.push/$1 [P]
    RewriteRule /websocket/(.*) ws://localhost:8080/websocket-demo/websocket/$1 [P]
</Location>
```

Websocket reconnections can easily be tested by restarting the Apache Web Server.
