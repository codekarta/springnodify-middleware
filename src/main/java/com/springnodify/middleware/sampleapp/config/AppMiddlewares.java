package com.springnodify.middleware.sampleapp.config;

import com.springnodify.middleware.core.Middleware;
import com.springnodify.middleware.core.MiddlewareType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class AppMiddlewares {

    @Middleware(order = 1)
    public boolean logging(HttpServletRequest req, HttpServletResponse res) {
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
    public boolean logStatus(HttpServletRequest req, HttpServletResponse res) {
        System.out.println("Response Status: " + res.getStatus());
        return true;
    }
}
