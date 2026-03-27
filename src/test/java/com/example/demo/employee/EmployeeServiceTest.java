package com.example.demo.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;

    // Reusable test fixture
    private Employee sampleEmployee;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository);

        sampleEmployee = new Employee("Jane", "Doe", "jane@example.com", "Engineering", new BigDecimal("95000.00"));
        // Simulate a persisted entity with an id
        sampleEmployee.setId(1L);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll returns list of responses mapped from all entities")
    void findAll_returnsAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(sampleEmployee));

        List<EmployeeResponse> result = employeeService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("jane@example.com");
        verify(employeeRepository).findAll();
    }

    @Test
    @DisplayName("findAll returns empty list when no employees exist")
    void findAll_noEmployees_returnsEmptyList() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        List<EmployeeResponse> result = employeeService.findAll();

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById returns response when employee exists")
    void findById_existingId_returnsResponse() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));

        EmployeeResponse result = employeeService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.firstName()).isEqualTo("Jane");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.email()).isEqualTo("jane@example.com");
        assertThat(result.department()).isEqualTo("Engineering");
        assertThat(result.salary()).isEqualByComparingTo("95000.00");
    }

    @Test
    @DisplayName("findById throws EmployeeNotFoundException when id does not exist")
    void findById_missingId_throwsNotFoundException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.findById(99L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("create saves entity and returns response with generated id")
    void create_validRequest_savesAndReturnsResponse() {
        EmployeeRequest request = new EmployeeRequest(
                "John", "Smith", "john@example.com", "Finance", new BigDecimal("80000.00"));

        Employee saved = new Employee("John", "Smith", "john@example.com", "Finance", new BigDecimal("80000.00"));
        saved.setId(2L);

        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        EmployeeResponse result = employeeService.create(request);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.email()).isEqualTo("john@example.com");
        verify(employeeRepository).save(any(Employee.class));
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update modifies fields and returns updated response")
    void update_existingId_updatesAndReturnsResponse() {
        EmployeeRequest request = new EmployeeRequest(
                "Jane", "Smith", "jane@example.com", "Engineering", new BigDecimal("100000.00"));

        Employee updated = new Employee("Jane", "Smith", "jane@example.com", "Engineering", new BigDecimal("100000.00"));
        updated.setId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updated);

        EmployeeResponse result = employeeService.update(1L, request);

        assertThat(result.lastName()).isEqualTo("Smith");
        assertThat(result.salary()).isEqualByComparingTo("100000.00");
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("update throws EmployeeNotFoundException when id does not exist")
    void update_missingId_throwsNotFoundException() {
        EmployeeRequest request = new EmployeeRequest(
                "Jane", "Smith", "jane@example.com", "Engineering", new BigDecimal("100000.00"));

        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.update(99L, request))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("delete calls repository deleteById when employee exists")
    void delete_existingId_deletesEmployee() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        employeeService.delete(1L);

        verify(employeeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws EmployeeNotFoundException when id does not exist")
    void delete_missingId_throwsNotFoundException() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.delete(99L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");
    }
}
