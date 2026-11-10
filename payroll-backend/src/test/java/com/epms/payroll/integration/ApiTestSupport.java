package com.epms.payroll.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Shared plumbing for every controller-level integration test: a full Spring Boot context
 * (all real beans, all real SQL) wired to the in-memory H2 "test" profile instead of the
 * developer's local MySQL database — so these tests never touch real data — plus a MockMvc
 * client for firing real HTTP requests at the real controllers.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public abstract class ApiTestSupport {

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * When a controller lets an exception escape with no @ControllerAdvice to catch it,
     * MockMvc does not turn that into a response with a 5xx status — it re-throws the
     * exception (wrapped in a ServletException) out of mockMvc.perform(...) itself. So the
     * "currently returns 500" negative tests in this suite assert with
     * assertThrows(Exception.class, () -> mockMvc.perform(...)) and then unwrap down to the
     * real cause with this helper, rather than chaining .andExpect(status().is5xxServerError()).
     */
    protected Throwable rootCause(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
