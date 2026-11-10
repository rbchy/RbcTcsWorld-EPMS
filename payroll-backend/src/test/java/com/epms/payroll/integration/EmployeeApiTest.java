package com.epms.payroll.integration;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Positive and negative coverage for the Employee CRUD + status-toggle endpoints.
 *
 * The two negative cases here used to assert on a raw, uncaught 500 (see the ApiTestSupport
 * Javadoc for why that showed up as an exception out of mockMvc.perform(...) rather than a
 * response). Now that GlobalExceptionHandler maps NoSuchElementException to 404 and
 * DataIntegrityViolationException to 400, both assert the proper response directly.
 */
@Epic("EPMS Payroll Backend")
@Feature("Employee Management")
class EmployeeApiTest extends ApiTestSupport {

    private Map<String, Object> newEmployeeBody(String number, String first, String last) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("employeeNumber", number);
        body.put("firstName", first);
        body.put("lastName", last);
        body.put("email", (first + "." + last + "@epms-demo.com").toLowerCase());
        body.put("department", "Quality Assurance");
        body.put("jobTitle", "QA Inspector");
        body.put("hourlyRate", 22.50);
        body.put("active", true);
        return body;
    }

    private int countEmployees() throws Exception {
        String body = mockMvc.perform(get("/api/employees")).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).size();
    }

    @Test
    @Tag("positive")
    @Story("Create and read")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A newly created employee can immediately be fetched back by id")
    void createThenGetById() throws Exception {
        String created = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeBody("QA-E001", "Alice", "Tester"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeNumber", is("QA-E001")))
                .andExpect(jsonPath("$.lastName", is("Tester")));
    }

    @Test
    @Tag("positive")
    @Story("Update in place")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PUT /api/employees/{id} updates the existing row instead of inserting a duplicate")
    void updateByIdModifiesTheSameRowNotADuplicate() throws Exception {
        int before = countEmployees();

        String created = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeBody("QA-E002", "Bob", "Builder"))))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("id").asLong();
        assertEquals(before + 1, countEmployees(), "creating one employee must add exactly one row");

        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeBody("QA-E002", "Bobby", "Builder"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Bobby")));

        assertEquals(before + 1, countEmployees(), "updating an existing employee by id must never insert a new row");

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Bobby")))
                .andExpect(jsonPath("$.employeeNumber", is("QA-E002")));
    }

    @Test
    @Tag("positive")
    @Story("Active/inactive toggle")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("PATCH /{id}/status flips the active flag and writes an audit-trail entry")
    void setActiveTogglesFlagAndAudits() throws Exception {
        String created = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeBody("QA-E003", "Cara", "Status"))))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(patch("/api/employees/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("active", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)));

        mockMvc.perform(get("/api/audit/entity").param("type", "EMPLOYEE").param("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action", is("STATUS_CHANGE")))
                .andExpect(jsonPath("$[0].newValue", is("false")));
    }

    @Test
    @Tag("negative")
    @Story("Missing data")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("GET an unknown employee id returns 404 Not Found")
    void getUnknownIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", 987654321L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("The requested resource does not exist.")));
    }

    @Test
    @Tag("negative")
    @Story("Missing data")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("POST with firstName/lastName omitted returns 400 Bad Request — there is no bean " +
            "validation layer, so the database's own NOT NULL constraint is what ultimately rejects it")
    void createWithMissingRequiredFieldReturnsBadRequest() throws Exception {
        Map<String, Object> incomplete = new LinkedHashMap<>();
        incomplete.put("employeeNumber", "QA-E999");
        // firstName and lastName are deliberately omitted — both are DB NOT NULL columns.

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomplete)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("FIRST_NAME")));
    }
}
