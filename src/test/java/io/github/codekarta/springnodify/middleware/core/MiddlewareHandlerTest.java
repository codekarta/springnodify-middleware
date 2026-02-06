package io.github.codekarta.springnodify.middleware.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiddlewareHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private TestMiddlewareBean testBean;

    @BeforeEach
    void setUp() {
        testBean = new TestMiddlewareBean();
    }

    @Test
    void testRunWithNoParameters() throws Exception {
        Method method = TestMiddlewareBean.class.getMethod("noParams");
        Middleware annotation = method.getAnnotation(Middleware.class);
        MiddlewareHandler handler = new MiddlewareHandler(testBean, method, annotation);

        boolean result = handler.run(request, response);

        assertTrue(result);
        assertTrue(testBean.noParamsCalled);
    }

    @Test
    void testRunWithOnlyRequest() throws Exception {
        Method method = TestMiddlewareBean.class.getMethod("onlyRequest", HttpServletRequest.class);
        Middleware annotation = method.getAnnotation(Middleware.class);
        MiddlewareHandler handler = new MiddlewareHandler(testBean, method, annotation);

        when(request.getRequestURI()).thenReturn("/test");
        boolean result = handler.run(request, response);

        assertTrue(result);
        assertTrue(testBean.onlyRequestCalled);
        verify(request).getRequestURI();
    }

    @Test
    void testRunWithOnlyResponse() throws Exception {
        Method method = TestMiddlewareBean.class.getMethod("onlyResponse", HttpServletResponse.class);
        Middleware annotation = method.getAnnotation(Middleware.class);
        MiddlewareHandler handler = new MiddlewareHandler(testBean, method, annotation);

        when(response.getStatus()).thenReturn(200);
        boolean result = handler.run(request, response);

        assertTrue(result);
        assertTrue(testBean.onlyResponseCalled);
        verify(response).getStatus();
    }

    @Test
    void testRunWithBothParameters() throws Exception {
        Method method = TestMiddlewareBean.class.getMethod("bothParams", HttpServletRequest.class, HttpServletResponse.class);
        Middleware annotation = method.getAnnotation(Middleware.class);
        MiddlewareHandler handler = new MiddlewareHandler(testBean, method, annotation);

        when(request.getMethod()).thenReturn("GET");
        boolean result = handler.run(request, response);

        assertTrue(result);
        assertTrue(testBean.bothParamsCalled);
        verify(request).getMethod();
        verify(response).setStatus(200);
    }

    @Test
    void testRunWithBothParametersReversedOrder() throws Exception {
        Method method = TestMiddlewareBean.class.getMethod("bothParamsReversed", HttpServletResponse.class, HttpServletRequest.class);
        Middleware annotation = method.getAnnotation(Middleware.class);
        MiddlewareHandler handler = new MiddlewareHandler(testBean, method, annotation);

        when(request.getRequestURI()).thenReturn("/test");
        boolean result = handler.run(request, response);

        assertTrue(result);
        assertTrue(testBean.bothParamsReversedCalled);
        verify(request).getRequestURI();
        verify(response).setStatus(201);
    }

    @Test
    void testRunWithUnsupportedParameterType() throws Exception {
        Method method = TestMiddlewareBean.class.getMethod("unsupportedParam", String.class);
        Middleware annotation = method.getAnnotation(Middleware.class);
        MiddlewareHandler handler = new MiddlewareHandler(testBean, method, annotation);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> handler.run(request, response)
        );

        assertTrue(exception.getMessage().contains("unsupported parameter type"));
        assertTrue(exception.getMessage().contains("String"));
        assertTrue(exception.getMessage().contains("unsupportedParam"));
    }

    @Test
    void testRunReturnsFalse() throws Exception {
        Method method = TestMiddlewareBean.class.getMethod("returnsFalse");
        Middleware annotation = method.getAnnotation(Middleware.class);
        MiddlewareHandler handler = new MiddlewareHandler(testBean, method, annotation);

        boolean result = handler.run(request, response);

        assertFalse(result);
    }

    // Test bean class with various method signatures
    static class TestMiddlewareBean {
        boolean noParamsCalled = false;
        boolean onlyRequestCalled = false;
        boolean onlyResponseCalled = false;
        boolean bothParamsCalled = false;
        boolean bothParamsReversedCalled = false;

        @Middleware
        public boolean noParams() {
            noParamsCalled = true;
            return true;
        }

        @Middleware
        public boolean onlyRequest(HttpServletRequest req) {
            onlyRequestCalled = true;
            req.getRequestURI(); // Use the parameter
            return true;
        }

        @Middleware
        public boolean onlyResponse(HttpServletResponse res) {
            onlyResponseCalled = true;
            res.getStatus(); // Use the parameter
            return true;
        }

        @Middleware
        public boolean bothParams(HttpServletRequest req, HttpServletResponse res) {
            bothParamsCalled = true;
            req.getMethod(); // Use request
            res.setStatus(200); // Use response
            return true;
        }

        @Middleware
        public boolean bothParamsReversed(HttpServletResponse res, HttpServletRequest req) {
            bothParamsReversedCalled = true;
            req.getRequestURI(); // Use request
            res.setStatus(201); // Use response
            return true;
        }

        @Middleware
        public boolean unsupportedParam(String param) {
            return true;
        }

        @Middleware
        public boolean returnsFalse() {
            return false;
        }
    }
}
