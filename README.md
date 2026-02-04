# Spring Nodify Middleware

[![Maven Central](https://img.shields.io/maven-central/v/com.springnodify/middleware.svg)](https://search.maven.org/artifact/com.springnodify/middleware)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**Spring Nodify Middleware** is a lightweight, non-intrusive library for Spring Boot that brings Node.js/Express-style middleware patterns to Java applications. It allows you to define request/response processing logic using simple annotations and a familiar functional approach.

## Features

- 🚀 **Express-like**: Familiar `(req, res) -> proceed` pattern.
- 🎯 **Annotation-based**: Just use `@Middleware` on any bean method.
- 🖇️ **Ordered Execution**: Easily control the execution order of your middlewares.
- 🛣️ **Path Matching**: Support for Ant-style path patterns (e.g., `/api/**`).
- 🔄 **Before/After Hooks**: Run logic before or after the main controller execution.

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.springnodify</groupId>
    <artifactId>middleware</artifactId>
    <version>1.0.0</version>
</dependency>
```

> [!NOTE]
> Since this library is a peer dependency for Spring Boot, make sure you have `spring-boot-starter-web` included in your project.

### Gradle

```groovy
implementation 'com.springnodify:middleware:1.0.0'
```

## How to Use

### 1. Define Middleware

Create a component and annotate your methods with `@Middleware`. Methods should accept `HttpServletRequest` and `HttpServletResponse` and return a `boolean`.

```java
@Component
public class AppMiddlewares {

    // Runs for all requests by default
    @Middleware(order = 1)
    public boolean logging(HttpServletRequest req, HttpServletResponse res) {
        System.out.println("Processing: " + req.getMethod() + " " + req.getRequestURI());
        return true; // Continue to next middleware/controller
    }

    // Runs only for private API paths
    @Middleware(url = "/api/private/**", order = 2)
    public boolean auth(HttpServletRequest req, HttpServletResponse res) {
        String apiKey = req.getHeader("X-API-KEY");
        if ("secret".equals(apiKey)) {
            return true;
        }
        res.setStatus(401);
        return false; // Stop execution and return 401
    }

    // Runs after the controller finishes
    @Middleware(order = 1, type = MiddlewareType.AFTER)
    public boolean postProcess(HttpServletRequest req, HttpServletResponse res) {
        System.out.println("Response Status: " + res.getStatus());
        return true;
    }
}
```

### 2. Registering the Filter

The library automatically registers its filter if you are using Spring Boot's auto-configuration. Ensure your application scans the `com.springnodify.middleware` package or that the library is correctly imported.

## Compatibility

- **Java**: 21+
- **Spring Boot**: 4.x+

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
