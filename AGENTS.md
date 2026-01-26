# Enterprise JSF Project - Agent Guidelines

## Build/Lint/Test Commands

### General Build
- `mvn clean install` - Build the entire project
- `make` - Build and deploy demo applications via Docker
- `make clean` - Clean the Maven project and stop Docker containers
- `make mrproper` - Clean everything and remove Docker images
- `make run-ee8` - Run Java EE 8 demo via Docker
- `make run-ee10` - Run Jakarta EE 10 demo via Docker

### Integration Tests
- `cd ejsf-tests && mvn clean test -Pintegration-tests` - Run Selenium based integration tests

### Single Test Execution
- `cd ejsf-tests && mvn test -Dtest=TestClass#testMethod` - Run a specific test method
- `cd ejsf-tests && mvn test -Dtest=TestClass` - Run all tests in a specific class
- `cd ejsf-demo && mvn test` - Run tests in demo module

### Code Quality
- `mvn compile` - Compile the project
- `mvn test` - Run tests
- `mvn clean package` - Package the project

## Code Style Guidelines

### Java
- Source and target compatibility: Java 8
- Use UTF-8 encoding
- Follow Java naming conventions (PascalCase for classes, camelCase for methods/fields)
- Imports should be organized with standard Java imports first, then third-party imports
- Use meaningful variable and method names
- All classes and methods should have Javadoc documentation
- Error handling: Use try-catch blocks appropriately and make sure to handle exceptions properly

### Maven
- All dependencies should be defined in `dependencyManagement` section
- Use appropriate scopes (provided, compile, runtime, test)
- Follow Maven conventions for project structure
- Use proper versioning with properties for common dependencies

### General
- All source code must be well documented
- Use consistent indentation (2 spaces)
- Maintain code readability and avoid overly complex expressions
- Follow component-based architecture principles

## Cursor Rules
No specific Cursor rules found in this repository.

## Copilot Instructions
No specific Copilot instructions found in this repository.