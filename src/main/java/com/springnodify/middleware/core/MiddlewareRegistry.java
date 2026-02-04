package com.springnodify.middleware.core;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class MiddlewareRegistry implements SmartInitializingSingleton {

    private final ApplicationContext ctx;
    private final List<MiddlewareHandler> handlers = new ArrayList<>();

    public MiddlewareRegistry(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (String name : ctx.getBeanDefinitionNames()) {
            Object bean = ctx.getBean(name);

            for (Method m : bean.getClass().getDeclaredMethods()) {
                Middleware mw = m.getAnnotation(Middleware.class);
                if (mw != null) {
                    handlers.add(new MiddlewareHandler(bean, m, mw));
                }
            }
        }

        handlers.sort(Comparator.comparingInt(h -> h.order));
    }

    public List<MiddlewareHandler> getHandlers() {
        return handlers;
    }
}
