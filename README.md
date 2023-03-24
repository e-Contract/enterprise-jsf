# Enterprise JSF

The Enterprise JSF project delivers:
* a PDF document about writing custom JSF components.
* a JSF component library (requires PrimeFaces).

The demo web application is available at:
https://demo.e-contract.be/ejsf-demo/


## The document

The latest version of the Enterprise JSF document is available at:

https://github.com/e-Contract/enterprise-jsf/releases/download/enterprise-jsf-1.4.0/enterprise-jsf.pdf

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
    <version>1.4.0</version>
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
    <version>1.4.0</version>
    <classifier>jakarta</classifier>
</dependency>
```

### Running the demo web application

We use Apache Maven as build system.
Apache Maven can be downloaded from: https://maven.apache.org

You need Java JDK 11 or higher to build this project.

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

You can also run the demo web application using an embedded Jetty servlet container.
Run the demo web application with Mojarra as JSF implementation via:
```
cd ejsf-demo
mvn clean jetty:run -Pmojarra
```
Run the demo web application with MyFaces as JSF implementation via:
```
cd ejsf-demo
mvn clean jetty:run -Pmyfaces
```

Alternatively, the Java EE application can be compiled and deployed to a WildFly Docker container
that is built on the fly (using the `Dockerfile`-files located in `docker/`) by entering the following command:
```shell
make
```

Other useful `make` targets are:
* `make run-ee10`: to deploy the JEE 10 version on a WildFly Docker 27.x container
* `make docker-start-ee8`: to start the WildFly Docker 26.x container, with l/p: admin/e-contract.be
* `make docker-start-ee10`: to start the WildFly Docker 27.x container, with l/p: admin/e-contract.be
* `make clean`: to clean the maven project and to stop the running WildFly Docker container
* `make mrproper`: to clean everything and remove the WildFly Docker image(s)
* `make doc-with-docker`: uses Docker with all required dependencies installed to generate the `enterprise-jsf.pdf` file.
