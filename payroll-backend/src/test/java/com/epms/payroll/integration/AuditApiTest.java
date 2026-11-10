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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Positive and negative coverage for /api/audit — the append-only audit trail. */
@Epic("EPMS Payroll Backend")
@Feature("Audit Trail")
class AuditApiTest extends ApiTestSupport {

    private long createEmployeeAndToggleStatus(String number) throws Exception {
        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeNumber", number);
        employee.put("firstName", "Audit");
        employee.put("lastName", "Fixture");
        employee.put("active", true);
        String created = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(patch("/api/employees/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("active", false))))
                .andExpect(status().isOk());
        return id;
    }

    @Test
    @Tag("positive")
    @Story("Global audit feed")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("A status change is recorded and shows up as the most recent /api/audit entry")
    void statusChangeAppearsAtTheTopOfTheGlobalAuditFeed() throws Exception {
        createEmployeeAndToggleStatus("AUD-E001");

        mockMvc.perform(get("/api/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityType", is("EMPLOYEE")))
                .andExpect(jsonPath("$[0].action", is("STATUS_CHANGE")));
    }

    @Test
    @Tag("positive")
    @Story("Per-entity audit history")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("/api/audit/entity filters strictly to the requested entity type and id")
    void auditEntityFiltersToThatOneEmployee() throws Exception {
        createEmployeeAndToggleStatus("AUD-E002");
        long id = createEmployeeAndToggleStatus("AUD-E003");

        mockMvc.perform(get("/api/audit/entity").param("type", "EMPLOYEE").param("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fieldName", is("active")))
                .andExpect(jsonPath("$[0].oldValue", is("true")))
                .andExpect(jsonPath("$[0].newValue", is("false")));
    }

    @Test
    @Tag("negative")
    @Story("Graceful empty results")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Requesting audit history for an entity id that was never touched returns 200 + an empty list")
    void auditEntityForUntouchedIdReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/audit/entity").param("type", "EMPLOYEE").param("id", "987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
