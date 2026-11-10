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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Positive and negative coverage for /api/w2. Reuses the same hand-verified fixture as
 * PayrollApiTest / TaxApiTest: gross 800.00, federal 80.00, SS 49.60, Medicare 11.60.
 *
 * The negative case here used to assert on a raw, uncaught 500 (see the ApiTestSupport Javadoc
 * for why that showed up as an exception out of mockMvc.perform(...) rather than a response).
 * Now that GlobalExceptionHandler maps NoSuchElementException to 404, it asserts the proper
 * response directly.
 */
@Epic("EPMS Payroll Backend")
@Feature("W-2 Generation")
class W2ApiTest extends ApiTestSupport {

    private long createEmployeeAndSaveKnownPayroll(String number) throws Exception {
        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeNumber", number);
        employee.put("firstName", "W2");
        employee.put("lastName", "Fixture");
        employee.put("hourlyRate", 20.00);
        employee.put("active", true);
        String created = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("id").asLong();

        LocalDate today = LocalDate.now();
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("employeeId", id);
        request.put("periodBeginning", today.minusDays(6).toString());
        request.put("periodEnding", today.toString());
        request.put("payDate", today.toString());
        request.put("regularHours", 40);
        request.put("doubletimeHours", 0);
        request.put("extraHours1", 0);
        request.put("extraHours2", 0);
        request.put("extraRate1", 0);
        request.put("extraRate2", 0);
        request.put("federalRate", 10);
        request.put("socialSecurityRate", 6.2);
        request.put("medicareRate", 1.45);
        request.put("paRate", 0);
        request.put("localRate", 0);
        request.put("lst", 0);
        request.put("paSuiRate", 0);
        request.put("employerMatchRate", 0);
        mockMvc.perform(post("/api/payroll/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        return id;
    }

    @Test
    @Tag("positive")
    @Story("Single-employee W-2")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /api/w2/{employeeId}/{year} returns box totals that match the one payroll run saved")
    void w2ForCurrentYearMatchesTheSinglePayrollRunSaved() throws Exception {
        long id = createEmployeeAndSaveKnownPayroll("W2-E001");
        int year = LocalDate.now().getYear();

        String body = mockMvc.perform(get("/api/w2/{employeeId}/{year}", id, year))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.box1Wages", closeTo(800.0, 0.01)))
                .andExpect(jsonPath("$.box2FederalTax", closeTo(80.0, 0.01)))
                .andExpect(jsonPath("$.box3SsWages", closeTo(800.0, 0.01)))
                .andExpect(jsonPath("$.box4SsTax", closeTo(49.6, 0.01)))
                .andExpect(jsonPath("$.box6MedicareTax", closeTo(11.6, 0.01)))
                .andReturn().getResponse().getContentAsString();

        assertEquals(1, objectMapper.readTree(body).get("payrollCount").asInt());
    }

    @Test
    @Tag("positive")
    @Story("Company-wide W-2 run")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /api/w2/year/{year} includes the employee that had a payroll run this year")
    void byYearIncludesTheEmployeeWithAPayrollThisYear() throws Exception {
        long id = createEmployeeAndSaveKnownPayroll("W2-E002");
        int year = LocalDate.now().getYear();

        String body = mockMvc.perform(get("/api/w2/year/{year}", year))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        boolean found = false;
        for (var row : objectMapper.readTree(body)) {
            if (row.get("employeeId").asLong() == id) found = true;
        }
        assertTrue(found, "the employee that had a payroll run this year must appear in the company-wide W-2 list");
    }

    @Test
    @Tag("negative")
    @Story("Missing data")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /api/w2/{employeeId}/{year} for an unknown employee id returns 404 Not Found")
    void w2ForUnknownEmployeeReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/w2/{employeeId}/{year}", 987654321L, LocalDate.now().getYear()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("The requested resource does not exist.")));
    }
}
