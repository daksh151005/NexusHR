CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    location VARCHAR(120) NOT NULL
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(140) NOT NULL,
    role_name VARCHAR(40) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(40) NOT NULL UNIQUE,
    full_name VARCHAR(160) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    job_title VARCHAR(120) NOT NULL,
    department_id BIGINT NOT NULL REFERENCES departments(id),
    manager_id BIGINT REFERENCES employees(id),
    employment_status VARCHAR(40) NOT NULL,
    work_location VARCHAR(120) NOT NULL,
    hire_date DATE NOT NULL,
    base_salary NUMERIC(14,2) NOT NULL,
    engagement_score INTEGER NOT NULL,
    skill_profile VARCHAR(500),
    document_status VARCHAR(80) NOT NULL
);

CREATE TABLE attendance_records (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    attendance_date DATE NOT NULL,
    check_in TIMESTAMP,
    check_out TIMESTAMP,
    status VARCHAR(40) NOT NULL,
    source VARCHAR(80) NOT NULL,
    UNIQUE(employee_id, attendance_date)
);

CREATE TABLE leave_requests (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    leave_type VARCHAR(80) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(40) NOT NULL,
    approver_comment VARCHAR(500),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE payroll_runs (
    id BIGSERIAL PRIMARY KEY,
    cycle VARCHAR(40) NOT NULL UNIQUE,
    gross_payroll NUMERIC(14,2) NOT NULL,
    net_payroll NUMERIC(14,2) NOT NULL,
    processed_employees INTEGER NOT NULL,
    status VARCHAR(40) NOT NULL,
    processed_at TIMESTAMP
);

CREATE TABLE payslips (
    id BIGSERIAL PRIMARY KEY,
    payroll_run_id BIGINT NOT NULL REFERENCES payroll_runs(id),
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    gross_salary NUMERIC(14,2) NOT NULL,
    tax_deduction NUMERIC(14,2) NOT NULL,
    benefit_deduction NUMERIC(14,2) NOT NULL,
    net_salary NUMERIC(14,2) NOT NULL,
    download_url VARCHAR(255),
    UNIQUE(payroll_run_id, employee_id)
);

CREATE TABLE performance_reviews (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    review_period VARCHAR(40) NOT NULL,
    manager_score INTEGER NOT NULL,
    peer_score INTEGER NOT NULL,
    goal_completion INTEGER NOT NULL,
    performance_band VARCHAR(80) NOT NULL,
    feedback VARCHAR(1000)
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    channel VARCHAR(40) NOT NULL,
    recipient VARCHAR(160) NOT NULL,
    subject VARCHAR(180) NOT NULL,
    body VARCHAR(1000) NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    actor VARCHAR(160) NOT NULL,
    action VARCHAR(120) NOT NULL,
    entity_type VARCHAR(80) NOT NULL,
    entity_id VARCHAR(80) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
