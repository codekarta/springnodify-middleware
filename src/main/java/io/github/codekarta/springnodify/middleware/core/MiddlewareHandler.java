package io.github.codekarta.springnodify.middleware.core;


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

    boolean doesNotMatch(String path) {
        return !matches(path);
    }

    boolean run(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            
            if (paramType == HttpServletRequest.class) {
                args[i] = req;
            } else if (paramType == HttpServletResponse.class) {
                args[i] = res;
            } else {
                throw new IllegalArgumentException(
                    String.format("Middleware method '%s' has unsupported parameter type '%s'. " +
                        "Only HttpServletRequest and HttpServletResponse are allowed.",
                        method.getName(), paramType.getName()));
            }
        }
        
        return (boolean) method.invoke(bean, args);
    }
}
