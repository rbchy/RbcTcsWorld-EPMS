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
 * Positive and negative coverage for /api/tax. Reuses the same hand-verified fixture as
 * PayrollApiTest: gross 800.00, federal 80.00, SS 49.60, Medicare 11.60, net 658.80.
 */
@Epic("EPMS Payroll Backend")
@Feature("Year-to-Date Tax Summary")
class TaxApiTest extends ApiTestSupport {

    private long createEmployeeAndSaveKnownPayroll(String number) throws Exception {
        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeNumber", number);
        employee.put("firstName", "Tax");
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
    @Story("Per-employee YTD summary")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /api/tax/ytd/{employeeId} sums the year's payroll into the expected YTD figures")
    void ytdSummaryMatchesTheSinglePayrollRunSaved() throws Exception {
        long id = createEmployeeAndSaveKnownPayroll("TAX-E001");

        mockMvc.perform(get("/api/tax/ytd/{employeeId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ytdGrossPay", closeTo(800.0, 0.01)))
                .andExpect(jsonPath("$.ytdNetPay", closeTo(658.8, 0.01)))
                .andExpect(jsonPath("$.ytdFederalTax", closeTo(80.0, 0.01)))
                .andExpect(jsonPath("$.ytdSocialSecurityTax", closeTo(49.6, 0.01)))
                .andExpect(jsonPath("$.ytdMedicareTax", closeTo(11.6, 0.01)))
                .andExpect(jsonPath("$.remainingSsWageRoom", closeTo(167800.0, 0.01)));
    }

    @Test
    @Tag("positive")
    @Story("Company-wide YTD summary")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /api/tax/ytd-all includes the employee that had a payroll run this year")
    void ytdAllIncludesTheEmployeeWithAPayrollThisYear() throws Exception {
        long id = createEmployeeAndSaveKnownPayroll("TAX-E002");

        String body = mockMvc.perform(get("/api/tax/ytd-all"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        boolean found = false;
        for (var row : objectMapper.readTree(body)) {
            if (row.get("employeeId").asLong() == id) {
                found = true;
                assertEquals(800.0, row.get("ytdGrossPay").asDouble(), 0.01);
            }
        }
        assertTrue(found, "the employee that had a payroll run this year must appear in ytd-all");
    }

    @Test
    @Tag("negative")
    @Story("Graceful handling of unknown employees")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("GET /api/tax/ytd/{employeeId} for an unknown employee id returns 200 with an all-zero " +
            "summary rather than a 404 — the controller never checks that the employee actually exists")
    void ytdForUnknownEmployeeReturnsZeroedSummaryNotAnError() throws Exception {
        mockMvc.perform(get("/api/tax/ytd/{employeeId}", 987654321L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ytdGrossPay", is(0)))
                .andExpect(jsonPath("$.remainingSsWageRoom", closeTo(168600.0, 0.01)));
    }
}
