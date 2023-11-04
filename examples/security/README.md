# JSF JASPIC Example

This example demonstrates how to integrate JASPIC 1.1 within a JSF web application.

The example works on different application servers.

## JBoss EAP/WildFly

Deploy on a local running JBoss EAP/WildFly via:
```
mvn clean wildfly:deploy
```

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
