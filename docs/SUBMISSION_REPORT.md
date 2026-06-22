# NexusHR Submission Report

## Overview

NexusHR is a Java full-stack HR and workforce intelligence platform covering employee lifecycle operations, attendance tracking, leave approvals, payroll processing, performance reviews, AI-style insights, notifications, audit logs, and operational monitoring.

## Feature Coverage

| ID | Requirement | Implementation |
| --- | --- | --- |
| F-01 | Employee lifecycle | JPA-backed employee records, create/update/offboard endpoints, department mapping |
| F-02 | Attendance and leave | Attendance records, leave requests, manager approval/rejection workflow |
| F-03 | Payroll processing | Payroll run endpoint, salary/tax/benefit deductions, generated payslip records |
| F-04 | Performance management | Performance reviews with manager score, peer score, goal completion, band |
| F-05 | AI workforce insights | Heuristic attrition risk, skill gap, engagement recommendations; Spring AI integration point documented |
| F-06 | Admin and manager dashboards | JWT-protected frontend operations console with role-aware controls |
| F-07 | Notifications | Notification records for payroll and leave decisions; mail/WebSocket dependencies included |

## Technology Stack

| Layer | Technology |
| --- | --- |
| Backend | Java 17-compatible Spring Boot 3.3, Spring Security, JPA/Hibernate |
| Database | PostgreSQL 17 in Docker Compose, H2 for local zero-setup development |
| Migrations | Flyway |
| Frontend | React, TypeScript, Vite |
| Auth | JWT, Argon2 password hashing, role authorities |
| Ops | Docker, Kubernetes manifests, GitHub Actions, Actuator, Prometheus, Grafana |

## Architecture

```text
React/Vite UI
    |
JWT REST API
    |
Spring Boot Controllers
    |
Services: Auth, HR workflows, payroll, insights, notifications, audit
    |
JPA repositories
    |
PostgreSQL + Flyway migrations
```

## Runbook

```bash
npm run install:frontend
npm run dev:backend
npm run dev:frontend
```

Docker path:

```bash
docker compose up --build
```

## Demo Accounts

| Email | Password | Role |
| --- | --- | --- |
| admin@nexushr.io | password123 | ADMIN |
| manager@nexushr.io | password123 | MANAGER |
| employee@nexushr.io | password123 | EMPLOYEE |

## Remaining Production Hardening

- Replace heuristic AI with Spring AI provider calls after adding an API key.
- Add real SMS provider credentials and domain email service.
- Add public HTTPS hosting, demo video, screenshots, and final PDF export.
- Run OWASP ZAP and load testing against the deployed URL.
