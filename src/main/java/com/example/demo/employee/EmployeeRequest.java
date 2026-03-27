package com.example.demo.employee;

import java.math.BigDecimal;

public record EmployeeRequest(
        String firstName,
        String lastName,
        String email,
        String department,
        BigDecimal salary) {
}
