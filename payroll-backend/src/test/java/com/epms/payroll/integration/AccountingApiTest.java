package com.epms.payroll.integration;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Positive and negative coverage for /api/accounting: the auto-seeded chart of accounts, and the
 * double-entry journal that PayrollAccountingService posts every time a payroll is saved.
 */
@Epic("EPMS Payroll Backend")
@Feature("General Ledger Accounting")
class AccountingApiTest extends ApiTestSupport {

    private long createHourlyEmployeeAndSavePayroll(String number) throws Exception {
        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeNumber", number);
        employee.put("firstName", "Ledger");
        employee.put("lastName", "Fixture");
        employee.put("hourlyRate", 20.00);
        employee.put("active", true);
        String createdEmployee = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andReturn().getResponse().getContentAsString();
        long employeeId = objectMapper.readTree(createdEmployee).get("id").asLong();

        LocalDate today = LocalDate.now();
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("employeeId", employeeId);
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
        return employeeId;
    }

    private double sumField(String journalJson, String field) throws Exception {
        double total = 0;
        for (var entry : objectMapper.readTree(journalJson)) {
            for (var line : entry.get("lines")) {
                total += line.get(field).asDouble();
            }
        }
        return total;
    }

    @Test
    @Tag("positive")
    @Story("Chart of accounts")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET /api/accounting/balances always includes the auto-seeded chart of accounts")
    void balancesIncludeTheSeededChartOfAccounts() throws Exception {
        mockMvc.perform(get("/api/accounting/balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.code=='1000')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.code=='5000')]", hasSize(1)))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(13))));
    }

    @Test
    @Tag("positive")
    @Story("Double-entry integrity")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Every journal entry posted for a payroll run balances: total debits equal total credits")
    void journalEntryForANewPayrollBalances() throws Exception {
        long employeeId = createHourlyEmployeeAndSavePayroll("GL-E001");

        String journal = mockMvc.perform(get("/api/accounting/journal").param("employeeId", String.valueOf(employeeId)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        double debitTotal = sumField(journal, "debit");
        double creditTotal = sumField(journal, "credit");
        assertEquals(debitTotal, creditTotal, 0.001,
                "a payroll journal entry must always be a balanced double-entry posting");
        assertEquals(861.20, debitTotal, 0.01,
                "gross 800.00 + FICA employer match 61.20 (SS 49.60 + Medicare 11.60)");
    }

    @Test
    @Tag("negative")
    @Story("Graceful empty results")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Filtering the journal by an employee id that never posted anything returns 200 + an empty " +
            "list, not an error — a good contrast to the 5xx findings elsewhere in this suite")
    void journalForUnknownEmployeeReturnsEmptyListNotAnError() throws Exception {
        mockMvc.perform(get("/api/accounting/journal").param("employeeId", "987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
