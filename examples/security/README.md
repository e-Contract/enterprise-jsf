# JSF JASPIC Example

This example demonstrates how to integrate JASPIC 1.1 within a JSF web application.

The example works on different application servers.


## JBoss EAP/WildFly

JBoss EAP 7.4 already has the following definition under `subsystem` `urn:jboss:domain:security:2.0`:
```xml
<security-domain name="jaspitest" cache-type="default">
    <authentication-jaspi>
        <login-module-stack name="dummy">
            <login-module code="Dummy" flag="optional"/>
        </login-module-stack>
        <auth-module code="Dummy"/>
    </authentication-jaspi>
</security-domain>
```

Configure WildFly version 26.1.3 as follows.
Under the `urn:wildfly:elytron:15.1` subsystem, add the following security domain:
```xml
<security-domain name="JASPICDomain"/>
```
Under the `urn:jboss:domain:undertow:12.0` subsystem, add the following application security domain:
```xml
<application-security-domain name="jaspitest" security-domain="JASPICDomain" integrated-jaspi="false"/>
```
Under the `urn:jboss:domain:ejb3:9.0` subsystem, add the following application security domain:
```xml
<application-security-domain name="jaspitest" security-domain="JASPICDomain"/>
```

Deploy on a local running JBoss EAP/WildFly via:
```
mvn clean wildfly:deploy
```

The demo web application should be available at: http://localhost:8080/security/


## Open Liberty

Run on Open Liberty via:
```
mvn clean package liberty:run
```


## Payara

Run on Payara via:
```
mvn clean package payara-micro:start -Ppayara
```
