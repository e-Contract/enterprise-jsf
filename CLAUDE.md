# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Enterprise JSF is a JSF component library built on top of PrimeFaces. It targets Java EE 8 (javax.*) as its primary source, and automatically produces a `jakarta` classifier artifact via the Maven Shade Plugin that relocates `javax.*` → `jakarta.*` packages for Jakarta EE 10+ runtimes.

## Build Commands

```bash
# Full project build
mvn clean install

# Run Java EE 8 demo with embedded Jetty
cd ejsf-demo && mvn clean jetty:run -Pmojarra   # Mojarra JSF impl
cd ejsf-demo && mvn clean jetty:run -Pmyfaces    # MyFaces JSF impl

# Run Jakarta EE 10 demo with embedded Jetty
cd ejsf-demo-ee10 && mvn clean jetty:run -Pmojarra
cd ejsf-demo-ee10 && mvn clean jetty:run -Pmyfaces

# Jakarta EE 11
cd ejsf-demo-ee10 && mvn clean jetty:run -Pmojarra-ee11
cd ejsf-demo-ee10 && mvn clean jetty:run -Pmyfaces-ee11

# Deploy to local WildFly (EE8 or EE10)
cd ejsf-demo && mvn wildfly:deploy
cd ejsf-demo-ee10 && mvn wildfly:deploy

# Docker-based build and deployment
make              # build and deploy EE8 via Docker
make run-ee10     # build and deploy EE10 via Docker
make clean        # clean maven + stop Docker containers
make mrproper     # clean everything + remove Docker images

# Integration tests (requires a running demo application)
cd ejsf-tests && mvn clean test -Pintegration-tests

# Run a specific integration test
cd ejsf-tests && mvn test -Pintegration-tests -Dtest=TestClassName#testMethodName

# Build PDF documentation (requires latex + PlantUML)
cd doc && make
# Or using Docker:
make doc-with-docker
```

## Module Structure

- **`ejsf-api`** — Public API (metrics, etc.) exposed to consuming applications
- **`ejsf-taglib`** — Core JSF component library; source uses `javax.*` namespace; Shade plugin produces `jakarta` classifier
- **`ejsf-taglib-d3`**, **`ejsf-taglib-echarts`**, **`ejsf-taglib-highlight`**, **`ejsf-taglib-katex`**, **`ejsf-taglib-leaflet`**, **`ejsf-taglib-marked`**, **`ejsf-taglib-mermaid`**, **`ejsf-taglib-ol`**, **`ejsf-taglib-photoswipe`**, **`ejsf-taglib-visnetwork`**, **`ejsf-taglib-webauthn`** — Optional add-on libraries for specific JS libraries
- **`ejsf-demo`** — Java EE 8 demo WAR (WildFly 26.x, Jetty, Liberty, Payara)
- **`ejsf-demo-ee10`** — Jakarta EE 10/11 demo WAR (WildFly 39.x, Jetty, Liberty, Payara)
- **`ejsf-demo-quarkus`** — Quarkus demo
- **`ejsf-tests`** — Selenium/PrimeFaces-Selenium integration tests
- **`doc/`** — LaTeX source for the Enterprise JSF PDF document
- **`examples/`** — Standalone example projects referenced in the PDF

## Component Architecture

### Adding a New JSF Component

Each component lives under `ejsf-taglib/src/main/java/be/e_contract/ejsf/component/<name>/` and typically consists of:

1. **`XxxComponent.java`** — Extends `UIComponentBase` (or `UINamingContainer` for composite-style). Annotated with `@FacesComponent(XxxComponent.COMPONENT_TYPE)` and `@ResourceDependencies`. Declares `COMPONENT_TYPE = "ejsf.xxxComponent"` and `COMPONENT_FAMILY = "ejsf"`.
2. **`XxxRenderer.java`** — Extends `Renderer`, annotated with `@FacesRenderer`.
3. **`XxxTagHandler.java`** — Optional, for custom tag handling logic.

JavaScript/CSS resources go in:
- `ejsf-taglib/src/main/resources/META-INF/resources/ejsf/`

Tag declarations go in:
- `ejsf-taglib/src/main/resources/META-INF/ejsf.taglib.xml`
- Tag namespace: `urn:be:e-contract:ejsf`

Faces configuration (component/renderer registration) goes in:
- `ejsf-taglib/src/main/resources/META-INF/faces-config.xml`

### Jakarta EE Dual-Artifact Pattern

The project writes source against `javax.*` (Java EE 8). The Maven Shade Plugin in `ejsf-taglib/pom.xml` automatically produces a second JAR with classifier `jakarta` by relocating:
- `javax.faces` → `jakarta.faces`
- `javax.servlet` → `jakarta.servlet`
- `javax.el` → `jakarta.el`
- `window.jsf` → `window.faces`

**Do not** manually use `jakarta.*` imports in the `ejsf-taglib` source — rely on the Shade relocation.

## Key Conventions

- Java source/target compatibility: **Java 8** (build requires JDK 17+)
- Encoding: UTF-8
- All dependency versions managed in the root `pom.xml` `<dependencyManagement>` section
- Caffeine cache kept at 2.x for Java 8 compatibility
- Integration tests use Selenium with PrimeFaces-Selenium and WebDriverManager
- Demo pages in `ejsf-demo/src/main/webapp/*.xhtml` — one page per component/feature
