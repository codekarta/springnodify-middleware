package io.github.codekarta.springnodify.middleware.sampleapp.config;

import io.github.codekarta.springnodify.middleware.core.Middleware;
import io.github.codekarta.springnodify.middleware.core.MiddlewareType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * Sample middleware configuration class demonstrating how to use Spring Nodify Middleware.
 * This class is part of the sample application and is excluded from the published library.
 */
@Component
public class AppMiddlewares {

    @Middleware(order = 1)
    public boolean logging(HttpServletRequest req) {
        System.out.println(req.getMethod() + " " + req.getRequestURI());
        return true;
    }

    @Middleware(url = "/api/private/**", order = 2)
    public boolean auth(HttpServletRequest req, HttpServletResponse res) {
        boolean ok = checkAuth(req);

        if (!ok) {
            res.setStatus(401);
        }
        return ok;
    }

    private boolean checkAuth(HttpServletRequest req) {
        return false;
    }

    @Middleware(order = 1, type = MiddlewareType.AFTER)
    public boolean logStatus(HttpServletResponse res) {
        System.out.println("Response Status: " + res.getStatus());
        return true;
    }

    @Middleware(url = "/api/public/**", order = 0)
    public boolean alwaysAllow() {
        // Example middleware with no parameters - always allows the request
        return true;
    }
}
