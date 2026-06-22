# NexusHR

NexusHR is an AI-enabled enterprise HR and workforce intelligence platform implemented as a Java full-stack monorepo. It now includes JWT authentication, role-aware APIs, PostgreSQL-ready persistence, Flyway migrations, employee lifecycle workflows, attendance, leave approvals, payroll and payslips, performance reviews, AI-style workforce insights, notifications, audit logs, Docker, Kubernetes, CI, and monitoring scaffolding.

## Stack

- Java 17-compatible Spring Boot 3.3 backend
- React + TypeScript + Vite frontend
- PostgreSQL 17, Redis 7, Flyway, JPA/Hibernate
- Spring Security, JWT, Argon2 password hashing, role-based authorization
- Actuator, Prometheus, Grafana, Docker, Kubernetes, GitHub Actions

## Project Structure

- `backend`: REST API, security configuration, dashboard endpoints, and demo auth
- `frontend`: HR operations console and typed API client
- `k8s`: Kubernetes deployment, service, and HPA manifests
- `monitoring`: Prometheus and Grafana provisioning
- `docs`: submission report and feature coverage notes

## Demo Credentials

- `admin@nexushr.io` / `password123`
- `manager@nexushr.io` / `password123`
- `employee@nexushr.io` / `password123`

## Run Locally

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend expects the backend at `http://localhost:8080`.

### Full Stack With PostgreSQL, Redis, Prometheus, and Grafana

```bash
docker compose up --build
```

Services:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

## Available API Endpoints

- `POST /api/auth/login`
- `GET /api/dashboard/overview`
- `GET /api/employees`
- `POST /api/employees`
- `PUT /api/employees/{id}`
- `DELETE /api/employees/{id}`
- `GET /api/attendance`
- `GET /api/leaves`
- `POST /api/leaves/{id}/decision`
- `GET /api/payroll`
- `POST /api/payroll`
- `GET /api/payroll/{id}/payslips`
- `GET /api/performance`
- `GET /api/insights`
- `GET /api/notifications`
- `GET /api/audit-logs`

All non-auth endpoints require `Authorization: Bearer <jwt-from-login>`.

## Suggested Next Upgrades

1. Connect Spring AI to a real OpenAI or Hugging Face key for live attrition prediction.
2. Add public HTTPS deployment and demo video assets for final submission.
3. Run OWASP ZAP and load testing against the deployed environment.
