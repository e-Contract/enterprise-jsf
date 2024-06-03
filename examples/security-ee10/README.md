# JSF JASPIC Example

This example demonstrates how to integrate JASPIC 1.1 within a JSF web application on Jakarta EE 10.

The example works on different application servers.


## JBoss EAP/WildFly

Configure JBoss EAP version 8.0.0 or WildFly version 32.0.0 as follows.
Under the `urn:wildfly:elytron:18.0` subsystem, add the following security domain:
```xml
<security-domain name="JASPICDomain"/>
```
Notice for WildFly version 32.0.0 that the Elytron subsystem namespace was changed to `urn:wildfly:elytron:community:18.0`.

Under the `urn:jboss:domain:undertow:14.0` subsystem, add the following application security domain:
```xml
<application-security-domain name="jaspitest" security-domain="JASPICDomain" integrated-jaspi="false"/>
```

Under the `urn:jboss:domain:ejb3:10.0` subsystem, add the following application security domain:
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
