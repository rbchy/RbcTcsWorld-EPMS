package com.epms.payroll.integration;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Smoke test for the liveness endpoint used by uptime checks / load balancers. */
@Epic("EPMS Payroll Backend")
@Feature("Health Check")
class HealthApiTest extends ApiTestSupport {

    @Test
    @Tag("positive")
    @Story("Liveness endpoint")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET /api/health reports the application is UP")
    void healthReportsUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }
}
