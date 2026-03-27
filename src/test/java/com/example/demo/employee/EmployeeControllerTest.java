package com.example.demo.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.exception.GlobalExceptionHandler;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest({EmployeeController.class, GlobalExceptionHandler.class})
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    private EmployeeResponse sampleResponse;
    private EmployeeRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = new EmployeeResponse(
                1L, "Jane", "Doe", "jane@example.com", "Engineering", new BigDecimal("95000.00"));

        sampleRequest = new EmployeeRequest(
                "Jane", "Doe", "jane@example.com", "Engineering", new BigDecimal("95000.00"));
    }

    // -------------------------------------------------------------------------
    // GET /api/employees
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/employees returns 200 with list of employees")
    void findAll_returns200WithEmployeeList() throws Exception {
        when(employeeService.findAll()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].email").value("jane@example.com"));
    }

    @Test
    @DisplayName("GET /api/employees returns 200 with empty list when no employees")
    void findAll_noEmployees_returns200WithEmptyList() throws Exception {
        when(employeeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // -------------------------------------------------------------------------
    // GET /api/employees/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/employees/{id} returns 200 with employee when found")
    void findById_existingId_returns200() throws Exception {
        when(employeeService.findById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.department").value("Engineering"))
                .andExpect(jsonPath("$.salary").value(95000.00));
    }

    @Test
    @DisplayName("GET /api/employees/{id} returns 404 when employee not found")
    void findById_missingId_returns404() throws Exception {
        when(employeeService.findById(99L)).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found: id=99"));
    }

    // -------------------------------------------------------------------------
    // POST /api/employees
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/employees returns 201 with created employee")
    void create_validRequest_returns201() throws Exception {
        when(employeeService.create(any(EmployeeRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    // -------------------------------------------------------------------------
    // PUT /api/employees/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/employees/{id} returns 200 with updated employee")
    void update_existingId_returns200() throws Exception {
        EmployeeResponse updated = new EmployeeResponse(
                1L, "Jane", "Smith", "jane@example.com", "Engineering", new BigDecimal("100000.00"));

        when(employeeService.update(eq(1L), any(EmployeeRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.salary").value(100000.00));
    }

    @Test
    @DisplayName("PUT /api/employees/{id} returns 404 when employee not found")
    void update_missingId_returns404() throws Exception {
        when(employeeService.update(eq(99L), any(EmployeeRequest.class)))
                .thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(put("/api/employees/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found: id=99"));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/employees/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/employees/{id} returns 204 when employee deleted")
    void delete_existingId_returns204() throws Exception {
        doNothing().when(employeeService).delete(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} returns 404 when employee not found")
    void delete_missingId_returns404() throws Exception {
        doThrow(new EmployeeNotFoundException(99L)).when(employeeService).delete(99L);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found: id=99"));
    }
}
