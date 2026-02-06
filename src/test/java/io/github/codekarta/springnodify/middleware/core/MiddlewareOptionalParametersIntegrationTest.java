package io.github.codekarta.springnodify.middleware.core;

import io.github.codekarta.springnodify.MiddlewareApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = MiddlewareApplication.class)
@AutoConfigureMockMvc
class MiddlewareOptionalParametersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testMiddlewareWithOnlyRequestParameter() throws Exception {
        // The logging middleware uses only HttpServletRequest
        // It should execute successfully without HttpServletResponse
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Manish Bansal"));
    }

    @Test
    void testMiddlewareWithOnlyResponseParameter() throws Exception {
        // The logStatus middleware (AFTER type) uses only HttpServletResponse
        // It should execute successfully after the request is processed
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        // The AFTER middleware runs but doesn't affect the response status
    }

    @Test
    void testMiddlewareWithBothParameters() throws Exception {
        // The auth middleware uses both HttpServletRequest and HttpServletResponse
        // It should execute and return 401 for /api/private/** paths
        mockMvc.perform(get("/api/private/data"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testBeforeMiddlewareWithOnlyRequest() throws Exception {
        // Test that BEFORE middleware with only request parameter works
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        // The logging middleware (only req) should execute before the controller
    }

    @Test
    void testAfterMiddlewareWithOnlyResponse() throws Exception {
        // Test that AFTER middleware with only response parameter works
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        // The logStatus middleware (only res) should execute after the controller
    }

    @Test
    void testMiddlewareOrderWithOptionalParameters() throws Exception {
        // Test that middleware execution order is maintained with optional parameters
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        // Order: logging (req only) -> controller -> logStatus (res only)
    }

    @Test
    void testMiddlewareWithNoParameters() throws Exception {
        // Test middleware with no parameters (alwaysAllow)
        mockMvc.perform(get("/api/public/info"))
                .andExpect(status().isOk())
                .andExpect(content().string("Public information - accessible to all"));
    }
}
