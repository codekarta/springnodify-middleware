# Spring Nodify Middleware

[![Maven Central](https://img.shields.io/maven-central/v/io.github.codekarta/springnodify-middleware.svg)](https://search.maven.org/artifact/io.github.codekarta/springnodify-middleware)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**Spring Nodify Middleware** is a lightweight, non-intrusive library for Spring Boot that brings Node.js/Express-style middleware patterns to Java applications. It allows you to define request/response processing logic using simple annotations and a familiar functional approach.

## Features

- 🚀 **Express-like**: Familiar `(req, res) -> proceed` pattern.
- 🎯 **Annotation-based**: Just use `@Middleware` on any bean method.
- 🖇️ **Ordered Execution**: Easily control the execution order of your middlewares.
- 🛣️ **Path Matching**: Support for Ant-style path patterns (e.g., `/api/**`).
- 🔄 **Before/After Hooks**: Run logic before or after the main controller execution.
- ✨ **Optional Parameters**: Use only the parameters you need - `HttpServletRequest` and `HttpServletResponse` are optional.

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.codekarta</groupId>
    <artifactId>springnodify-middleware</artifactId>
    <version>1.0.1</version>
</dependency>
```

> [!NOTE]
> Since this library is a peer dependency for Spring Boot, make sure you have `spring-boot-starter-web` included in your project.

### Gradle

```groovy
implementation 'io.github.codekarta:springnodify-middleware:1.0.0'
```

## How to Use

### 1. Define Middleware

Create a component and annotate your methods with `@Middleware`. Methods should return a `boolean` and can optionally accept `HttpServletRequest` and/or `HttpServletResponse` parameters.

**Parameter Options:**
- No parameters: `boolean method()`
- Only request: `boolean method(HttpServletRequest req)`
- Only response: `boolean method(HttpServletResponse res)`
- Both parameters: `boolean method(HttpServletRequest req, HttpServletResponse res)`
- Both in any order: `boolean method(HttpServletResponse res, HttpServletRequest req)`

```java
@Component
public class AppMiddlewares {

    // BEFORE middleware - only needs request parameter
    @Middleware(order = 1)
    public boolean logging(HttpServletRequest req) {
        System.out.println("Processing: " + req.getMethod() + " " + req.getRequestURI());
        return true; // Continue to next middleware/controller
    }

    // BEFORE middleware - needs both parameters
    @Middleware(url = "/api/private/**", order = 2)
    public boolean auth(HttpServletRequest req, HttpServletResponse res) {
        String apiKey = req.getHeader("X-API-KEY");
        if ("secret".equals(apiKey)) {
            return true;
        }
        res.setStatus(401);
        return false; // Stop execution and return 401
    }

    // BEFORE middleware - no parameters needed
    @Middleware(url = "/api/public/**", order = 0)
    public boolean alwaysAllow() {
        // Simple middleware that always allows the request
        return true;
    }

    // AFTER middleware - only needs response parameter
    @Middleware(order = 1, type = MiddlewareType.AFTER)
    public boolean logStatus(HttpServletResponse res) {
        System.out.println("Response Status: " + res.getStatus());
        return true;
    }
}
```

### 2. Parameter Selection Guidelines

Choose parameters based on your middleware's needs:

- **BEFORE middleware** typically only needs `HttpServletRequest` for reading request data
- **AFTER middleware** typically only needs `HttpServletResponse` for reading response data
- Use both parameters when you need to read from request and write to response
- Use no parameters for simple logic that doesn't need request/response access

> [!TIP]
> The library automatically matches parameters by type, so you can use them in any order. Only `HttpServletRequest` and `HttpServletResponse` are supported as parameter types.

### 3. Registering the Filter

The library automatically registers its filter if you are using Spring Boot's auto-configuration. Ensure your application scans the `io.github.codekarta.springnodify.middleware.core` package or that the library is correctly imported.

## Compatibility

- **Java**: 21+
- **Spring Boot**: 4.x+

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
