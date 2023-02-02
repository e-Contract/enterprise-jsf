# Enterprise JSF

The Enterprise JSF project delivers:
* a PDF document about writing custom JSF components.
* a JSF component library (requires PrimeFaces).

## The document

Latest version of Enterprise JSF is available at:
https://github.com/e-Contract/enterprise-jsf/releases/download/2022-05-24/enterprise-jsf-2022-05-24.pdf

### Requirements

The following dependencies are required for building the PDF document:
* make
* latex
* PlantUML, and a `PLANTUML_JAR` environment variable pointing to the corresponding JAR.
PlantUML can be downloaded from: https://plantuml.com

Building this project should be possible on Mac OS, Linux, and FreeBSD.

### PDF

Compile the PDF document via:
```
make
```

We prefer to edit the LaTeX document using TeXstudio.
TeXstudio can be downloaded from:
https://texstudio.org
