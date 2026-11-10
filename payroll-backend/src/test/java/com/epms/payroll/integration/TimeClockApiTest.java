package com.epms.payroll.integration;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Positive and negative coverage for /api/time-entries clock-in / clock-out.
 *
 * The negative cases here used to assert on a raw, uncaught 500 (see the ApiTestSupport
 * Javadoc for why that showed up as an exception out of mockMvc.perform(...) rather than a
 * response). Now that GlobalExceptionHandler maps BusinessRuleException to 409 and
 * NoSuchElementException to 404, all three assert the proper response directly.
 */
@Epic("EPMS Payroll Backend")
@Feature("Time Clock")
class TimeClockApiTest extends ApiTestSupport {

    private long createEmployee(String number) throws Exception {
        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeNumber", number);
        employee.put("firstName", "Clock");
        employee.put("lastName", "Fixture");
        employee.put("active", true);
        String created = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(created).get("id").asLong();
    }

    @Test
    @Tag("positive")
    @Story("Clock in / clock out")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("clock-in opens an OPEN entry and clock-out closes it")
    void clockInThenClockOutOpensAndClosesTheEntry() throws Exception {
        long employeeId = createEmployee("TC-E001");

        String opened = mockMvc.perform(post("/api/time-entries/clock-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("employeeId", employeeId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andReturn().getResponse().getContentAsString();
        long entryId = objectMapper.readTree(opened).get("id").asLong();

        mockMvc.perform(post("/api/time-entries/{id}/clock-out", entryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CLOSED")));

        mockMvc.perform(get("/api/time-entries/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Tag("negative")
    @Story("State machine guard")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Clocking in twice for the same employee before clocking out returns 409 Conflict")
    void doubleClockInReturnsConflict() throws Exception {
        long employeeId = createEmployee("TC-E002");

        mockMvc.perform(post("/api/time-entries/clock-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("employeeId", employeeId))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/time-entries/clock-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("employeeId", employeeId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("This employee is already clocked in.")));
    }

    @Test
    @Tag("negative")
    @Story("State machine guard")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Clocking out an already-closed entry returns 409 Conflict")
    void clockOutOnAnAlreadyClosedEntryReturnsConflict() throws Exception {
        long employeeId = createEmployee("TC-E003");
        String opened = mockMvc.perform(post("/api/time-entries/clock-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("employeeId", employeeId))))
                .andReturn().getResponse().getContentAsString();
        long entryId = objectMapper.readTree(opened).get("id").asLong();

        mockMvc.perform(post("/api/time-entries/{id}/clock-out", entryId)).andExpect(status().isOk());

        mockMvc.perform(post("/api/time-entries/{id}/clock-out", entryId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("This time entry is already closed.")));
    }

    @Test
    @Tag("negative")
    @Story("Missing data")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Clocking out an unknown time-entry id returns 404 Not Found")
    void clockOutOnUnknownIdReturnsNotFound() throws Exception {
        mockMvc.perform(post("/api/time-entries/{id}/clock-out", 987654321L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("The requested resource does not exist.")));
    }
}
