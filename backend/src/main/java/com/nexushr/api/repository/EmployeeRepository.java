package com.nexushr.api.repository;

import com.nexushr.api.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    long countByEmploymentStatus(String employmentStatus);
}
