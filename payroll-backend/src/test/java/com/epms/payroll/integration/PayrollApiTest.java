package com.epms.payroll.integration;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Positive and negative coverage for /api/payroll. The fixture below (40 regular hours at a
 * $20.00/hr rate, 10% federal / 6.2% Social Security / 1.45% Medicare, everything else zero) is
 * hand-verified: gross = 800.00, federal = 80.00, SS = 49.60, Medicare = 11.60, net = 658.80.
 *
 * The two state-machine-guard negative cases here used to assert on a raw, uncaught 500 (see
 * the ApiTestSupport Javadoc for why that showed up as an exception out of mockMvc.perform(...)
 * rather than a response). Now that GlobalExceptionHandler maps BusinessRuleException to 409,
 * both assert the proper response directly.
 */
@Epic("EPMS Payroll Backend")
@Feature("Payroll Processing")
class PayrollApiTest extends ApiTestSupport {

    private long createHourlyEmployee(String number) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("employeeNumber", number);
        body.put("firstName", "Payroll");
        body.put("lastName", "Fixture");
        body.put("hourlyRate", 20.00);
        body.put("active", true);
        String created = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(created).get("id").asLong();
    }

    private Map<String, Object> plainWeekRequest(long employeeId) {
        LocalDate today = LocalDate.now();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("employeeId", employeeId);
        r.put("periodBeginning", today.minusDays(6).toString());
        r.put("periodEnding", today.toString());
        r.put("payDate", today.toString());
        r.put("regularHours", 40);
        r.put("doubletimeHours", 0);
        r.put("extraHours1", 0);
        r.put("extraHours2", 0);
        r.put("extraRate1", 0);
        r.put("extraRate2", 0);
        r.put("federalRate", 10);
        r.put("socialSecurityRate", 6.2);
        r.put("medicareRate", 1.45);
        r.put("paRate", 0);
        r.put("localRate", 0);
        r.put("lst", 0);
        r.put("paSuiRate", 0);
        r.put("employerMatchRate", 0);
        return r;
    }

    @Test
    @Tag("positive")
    @Story("Gross-to-net calculation")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("A plain 40-hour week at $20/hr with 10% federal + FICA computes the expected gross and net")
    void calculateProducesExpectedGrossAndNet() throws Exception {
        long id = createHourlyEmployee("PR-E001");

        mockMvc.perform(post("/api/payroll/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plainWeekRequest(id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grossPay", closeTo(800.0, 0.01)))
                .andExpect(jsonPath("$.netPay", closeTo(658.8, 0.01)));
    }

    @Test
    @Tag("positive")
    @Story("Persisting a payroll run")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /save persists the payroll and it shows up in that employee's payroll history")
    void saveThenAppearsInEmployeeHistory() throws Exception {
        long id = createHourlyEmployee("PR-E002");

        String saved = mockMvc.perform(post("/api/payroll/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plainWeekRequest(id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netPay", closeTo(658.8, 0.01)))
                .andReturn().getResponse().getContentAsString();
        long payrollId = objectMapper.readTree(saved).get("id").asLong();

        String history = mockMvc.perform(get("/api/payroll/employee/{id}", id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var rows = objectMapper.readTree(history);
        assertEquals(1, rows.size(), "this freshly-created employee must have exactly one payroll on file");
        assertEquals(payrollId, rows.get(0).get("id").asLong());
        assertEquals(658.8, rows.get(0).get("netPay").asDouble(), 0.01);
    }

    @Test
    @Tag("negative")
    @Story("State machine guard")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("A PROCESSED payroll cannot be voided — returns 409 Conflict, not a raw 500")
    void voidingAProcessedPayrollReturnsConflict() throws Exception {
        long id = createHourlyEmployee("PR-E003");
        String saved = mockMvc.perform(post("/api/payroll/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plainWeekRequest(id))))
                .andReturn().getResponse().getContentAsString();
        long payrollId = objectMapper.readTree(saved).get("id").asLong();

        mockMvc.perform(patch("/api/payroll/{id}/status", payrollId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "PROCESSED"))))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/payroll/{id}/void", payrollId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("reason", "test"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message",
                        is("A processed or already-reversed payroll cannot be voided — use Reverse instead.")));
    }

    @Test
    @Tag("negative")
    @Story("State machine guard")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("A payroll that was never PROCESSED cannot be reversed — returns 409 Conflict, not a raw 500")
    void reversingANonProcessedPayrollReturnsConflict() throws Exception {
        long id = createHourlyEmployee("PR-E004");
        String saved = mockMvc.perform(post("/api/payroll/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plainWeekRequest(id))))
                .andReturn().getResponse().getContentAsString();
        long payrollId = objectMapper.readTree(saved).get("id").asLong();

        mockMvc.perform(patch("/api/payroll/{id}/reverse", payrollId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("reason", "test"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Only a PROCESSED payroll can be reversed.")));
    }
}
