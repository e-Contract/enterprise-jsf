# Enterprise JSF

The Enterprise JSF project delivers:
* a PDF document about writing custom JSF components.
* a JSF component library (requires PrimeFaces).

## The document

The latest version of the Enterprise JSF document is available at:

https://github.com/e-Contract/enterprise-jsf/releases/download/2023-02-07/enterprise-jsf.pdf

The following dependencies are required for building the PDF document:
* make
* latex
* PlantUML, and a `PLANTUML_JAR` environment variable pointing to the corresponding JAR.
PlantUML can be downloaded from: https://plantuml.com

Building this project should be possible on Mac OS, Linux, and FreeBSD.

Compile the PDF document via:
```
cd doc
make
```

We prefer to edit the LaTeX document using TeXstudio.
TeXstudio can be downloaded from:
https://texstudio.org

## The JSF component library

The JSF component library is available within the e-Contract.be public Maven repository.
If you use Maven, refer to the e-Contract.be Maven repository via:
```xml
<repository>
    <id>e-contract</id>
    <url>https://www.e-contract.be/maven2/</url>
    <releases>
        <enabled>true</enabled>
    </releases>
</repository>
```
Add the JSF component library to your web application WAR project via:
```xml
<dependency>
    <groupId>be.e-contract.enterprise-jsf</groupId>
    <artifactId>ejsf-taglib</artifactId>
    <version>0.2.0</version>
</dependency>
```
Within you JSF pages, use the following JSF namespace declaration:
```
xmlns:ejsf="urn:be:e-contract:ejsf"
```
Now you are ready to use our components, for example:
```xml
<ejsf:outputBoolean value="#{true}"/>
```
For Jakarta EE 10 runtimes, use the following dependency:
```xml
<dependency>
    <groupId>be.e-contract.enterprise-jsf</groupId>
    <artifactId>ejsf-taglib</artifactId>
    <version>0.2.0</version>
    <classifier>jakarta</classifier>
</dependency>
```

### Running the demo web application

We use Apache Maven as build system.
Apache Maven can be downloaded from: https://maven.apache.org

Build the project via:
```
mvn clean install
```

Running the demo web application requires an application server that supports Java EE version 8.
We prefer WildFly as application server.
WildFly version 26.1.3.Final is the latest version that still supports Java EE 8.
WildFly can be downloaded from: https://www.wildfly.org

Start a WildFly version 26.1.3.Final via:
```
cd wildfly-26.1.3.Final/bin
./standalone.sh --server-config=standalone-full.xml
```

Deploy the demo web application via:
```
cd ejsf-demo
mvn wildfly:deploy
```

Try out the demo web application via:
http://localhost:8080/ejsf-demo/
