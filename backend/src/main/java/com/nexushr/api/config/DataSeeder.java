package com.nexushr.api.config;

import com.nexushr.api.model.AttendanceRecord;
import com.nexushr.api.model.Department;
import com.nexushr.api.model.Employee;
import com.nexushr.api.model.LeaveRequest;
import com.nexushr.api.model.PerformanceReview;
import com.nexushr.api.model.Role;
import com.nexushr.api.model.UserAccount;
import com.nexushr.api.repository.AttendanceRecordRepository;
import com.nexushr.api.repository.DepartmentRepository;
import com.nexushr.api.repository.EmployeeRepository;
import com.nexushr.api.repository.LeaveRequestRepository;
import com.nexushr.api.repository.PerformanceReviewRepository;
import com.nexushr.api.repository.RoleRepository;
import com.nexushr.api.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            DepartmentRepository departmentRepository,
            RoleRepository roleRepository,
            UserAccountRepository userAccountRepository,
            EmployeeRepository employeeRepository,
            AttendanceRecordRepository attendanceRecordRepository,
            LeaveRequestRepository leaveRequestRepository,
            PerformanceReviewRepository performanceReviewRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userAccountRepository.count() > 0) {
                return;
            }

            Role admin = role("ADMIN", "Platform administrator");
            Role manager = role("MANAGER", "People manager");
            Role employeeRole = role("EMPLOYEE", "Employee self-service user");
            roleRepository.saveAll(List.of(admin, manager, employeeRole));

            Department engineering = department("Engineering", "Bengaluru");
            Department hr = department("Human Resources", "Gurugram");
            Department analytics = department("Workforce Intelligence", "Pune");
            Department finance = department("Finance", "Noida");
            departmentRepository.saveAll(List.of(engineering, hr, analytics, finance));

            userAccountRepository.save(user("admin@nexushr.io", "Maya Singh", "ADMIN", passwordEncoder));
            userAccountRepository.save(user("manager@nexushr.io", "Rahul Mehta", "MANAGER", passwordEncoder));
            userAccountRepository.save(user("employee@nexushr.io", "Aarav Sharma", "EMPLOYEE", passwordEncoder));

            Employee aarav = employee("NX-1001", "Aarav Sharma", "aarav@nexushr.io", "Senior Engineer", engineering, "Active", "Bengaluru", new BigDecimal("2600000"), 92, "Java, Spring Boot, Kubernetes", "Verified");
            Employee isha = employee("NX-1002", "Isha Verma", "isha@nexushr.io", "HR Business Partner", hr, "Active", "Gurugram", new BigDecimal("1850000"), 88, "Employee relations, HR analytics", "Verified");
            Employee kabir = employee("NX-1003", "Kabir Nanda", "kabir@nexushr.io", "Data Analyst", analytics, "Remote", "Pune", new BigDecimal("1700000"), 84, "SQL, Python, dashboarding", "Pending");
            Employee neha = employee("NX-1004", "Neha Kapur", "neha@nexushr.io", "Finance Associate", finance, "On Leave", "Noida", new BigDecimal("1450000"), 79, "Payroll, compliance, tax", "Verified");
            employeeRepository.saveAll(List.of(aarav, isha, kabir, neha));

            attendanceRecordRepository.save(attendance(aarav, "Present", "Biometric"));
            attendanceRecordRepository.save(attendance(isha, "Present", "Biometric"));
            attendanceRecordRepository.save(attendance(kabir, "Remote", "Self-service"));
            attendanceRecordRepository.save(attendance(neha, "Leave", "Leave workflow"));

            leaveRequestRepository.save(leave(neha, "Annual Leave", "Approved", LocalDate.now().plusDays(3), LocalDate.now().plusDays(6)));
            leaveRequestRepository.save(leave(aarav, "Sick Leave", "Pending", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)));
            leaveRequestRepository.save(leave(kabir, "Work From Anywhere", "Approved", LocalDate.now().plusDays(9), LocalDate.now().plusDays(18)));

            performanceReviewRepository.save(review(aarav, "H1 2026", 5, 4, 96, "Exceeds Expectations"));
            performanceReviewRepository.save(review(isha, "H1 2026", 4, 5, 89, "Strong Performer"));
            performanceReviewRepository.save(review(kabir, "H1 2026", 4, 4, 86, "Strong Performer"));
        };
    }

    private Role role(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return role;
    }

    private Department department(String name, String location) {
        Department department = new Department();
        department.setName(name);
        department.setLocation(location);
        return department;
    }

    private UserAccount user(String email, String displayName, String role, PasswordEncoder passwordEncoder) {
        UserAccount account = new UserAccount();
        account.setEmail(email);
        account.setDisplayName(displayName);
        account.setRoleName(role);
        account.setPasswordHash(passwordEncoder.encode("password123"));
        account.setEnabled(true);
        return account;
    }

    private Employee employee(String code, String name, String email, String title, Department department, String status, String location, BigDecimal salary, int engagement, String skills, String documentStatus) {
        Employee employee = new Employee();
        employee.setEmployeeCode(code);
        employee.setFullName(name);
        employee.setEmail(email);
        employee.setJobTitle(title);
        employee.setDepartment(department);
        employee.setEmploymentStatus(status);
        employee.setWorkLocation(location);
        employee.setHireDate(LocalDate.now().minusYears(2));
        employee.setBaseSalary(salary);
        employee.setEngagementScore(engagement);
        employee.setSkillProfile(skills);
        employee.setDocumentStatus(documentStatus);
        return employee;
    }

    private AttendanceRecord attendance(Employee employee, String status, String source) {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployee(employee);
        record.setAttendanceDate(LocalDate.now());
        record.setCheckIn(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 35)));
        record.setCheckOut("Leave".equals(status) ? null : LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 10)));
        record.setStatus(status);
        record.setSource(source);
        return record;
    }

    private LeaveRequest leave(Employee employee, String type, String status, LocalDate start, LocalDate end) {
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setLeaveType(type);
        request.setStatus(status);
        request.setStartDate(start);
        request.setEndDate(end);
        request.setReason("Planned request");
        request.setApproverComment("Approved".equals(status) ? "Coverage confirmed" : null);
        request.setCreatedAt(LocalDateTime.now().minusDays(1));
        return request;
    }

    private PerformanceReview review(Employee employee, String period, int managerScore, int peerScore, int goalCompletion, String band) {
        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setReviewPeriod(period);
        review.setManagerScore(managerScore);
        review.setPeerScore(peerScore);
        review.setGoalCompletion(goalCompletion);
        review.setPerformanceBand(band);
        review.setFeedback("Consistent delivery with strong collaboration signal.");
        return review;
    }
}
