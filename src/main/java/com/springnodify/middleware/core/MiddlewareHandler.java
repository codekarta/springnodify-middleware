package com.springnodify.middleware.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MiddlewareHandler {

    final Object bean;
    final Method method;
    final String[] patterns;
    final int order;
    final MiddlewareType type;

    public MiddlewareHandler(Object bean, Method method, Middleware mw) {
        this.bean = bean;
        this.method = method;
        this.patterns = mw.url();
        this.order = mw.order();
        this.type = mw.type();
    }

    boolean matches(String path) {
        AntPathMatcher matcher = new AntPathMatcher();
        return Arrays.stream(patterns).anyMatch(p -> matcher.match(p, path));
    }

    boolean run(HttpServletRequest req, HttpServletResponse res) throws Exception {
        return (boolean) method.invoke(bean, req, res);
    }
}
