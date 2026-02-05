package io.github.codekarta.springnodify.middleware.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MiddlewareFilter extends OncePerRequestFilter {

    private final MiddlewareRegistry registry;

    public MiddlewareFilter(MiddlewareRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            @NonNull HttpServletResponse res,
            @NonNull FilterChain chain) throws ServletException {

        String path = req.getRequestURI();

        try {
            boolean keepGoing = true;

            // BEFORE Middleware
            for (MiddlewareHandler h : registry.getHandlers()) {
                if (h.type != MiddlewareType.BEFORE)
                    continue;
                if (h.doesNotMatch(path))
                    continue;

                boolean proceed = h.run(req, res);
                if (!proceed) {
                    keepGoing = false;
                    break;
                }
            }

            if (keepGoing) {
                chain.doFilter(req, res);
            }

            // AFTER Middleware
            for (MiddlewareHandler h : registry.getHandlers()) {
                if (h.type != MiddlewareType.AFTER)
                    continue;
                if (h.doesNotMatch(path))
                    continue;

                h.run(req, res); // Return value ignored for AFTER middleware
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
