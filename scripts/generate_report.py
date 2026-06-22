from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.platypus import (
    Flowable,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


OUTPUT = "docs/NexusHR_Project_Report.pdf"


class Rule(Flowable):
    def __init__(self, width=16 * cm, color=colors.HexColor("#147d6f")):
        super().__init__()
        self.width = width
        self.color = color
        self.height = 0.15 * cm

    def draw(self):
        self.canv.setStrokeColor(self.color)
        self.canv.setLineWidth(1.2)
        self.canv.line(0, 0, self.width, 0)


def header_footer(canvas, doc):
    canvas.saveState()
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(colors.HexColor("#617083"))
    canvas.drawString(1.5 * cm, 1 * cm, "NexusHR - AI-Enabled Enterprise HR & Workforce Intelligence Platform")
    canvas.drawRightString(19.5 * cm, 1 * cm, f"Page {doc.page}")
    canvas.restoreState()


def p(text, style):
    return Paragraph(text, style)


def section_title(text, styles):
    return [p(text, styles["SectionTitle"]), Rule(), Spacer(1, 0.35 * cm)]


def table(data, widths=None):
    t = Table(data, colWidths=widths, repeatRows=1)
    t.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#147d6f")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("FONTSIZE", (0, 0), (-1, -1), 8),
                ("LEADING", (0, 0), (-1, -1), 10),
                ("GRID", (0, 0), (-1, -1), 0.25, colors.HexColor("#d8dee6")),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#f3f6f8")]),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
                ("RIGHTPADDING", (0, 0), (-1, -1), 6),
                ("TOPPADDING", (0, 0), (-1, -1), 6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
            ]
        )
    )
    return t


def build():
    doc = SimpleDocTemplate(
        OUTPUT,
        pagesize=A4,
        rightMargin=1.45 * cm,
        leftMargin=1.45 * cm,
        topMargin=1.45 * cm,
        bottomMargin=1.45 * cm,
        title="NexusHR Project Report",
        author="Daksh Kumar",
    )

    base = getSampleStyleSheet()
    styles = {
        "CoverTitle": ParagraphStyle(
            "CoverTitle",
            parent=base["Title"],
            fontName="Helvetica-Bold",
            fontSize=30,
            leading=34,
            alignment=TA_CENTER,
            textColor=colors.HexColor("#18202a"),
            spaceAfter=18,
        ),
        "CoverSubtitle": ParagraphStyle(
            "CoverSubtitle",
            parent=base["BodyText"],
            fontSize=13,
            leading=18,
            alignment=TA_CENTER,
            textColor=colors.HexColor("#147d6f"),
            spaceAfter=28,
        ),
        "SectionTitle": ParagraphStyle(
            "SectionTitle",
            parent=base["Heading1"],
            fontName="Helvetica-Bold",
            fontSize=19,
            leading=23,
            textColor=colors.HexColor("#18202a"),
            spaceAfter=8,
        ),
        "Heading": ParagraphStyle(
            "Heading",
            parent=base["Heading2"],
            fontName="Helvetica-Bold",
            fontSize=12,
            leading=15,
            textColor=colors.HexColor("#243447"),
            spaceBefore=8,
            spaceAfter=5,
        ),
        "Body": ParagraphStyle(
            "Body",
            parent=base["BodyText"],
            fontSize=9.2,
            leading=13,
            textColor=colors.HexColor("#27313d"),
            alignment=TA_LEFT,
            spaceAfter=8,
        ),
        "Small": ParagraphStyle(
            "Small",
            parent=base["BodyText"],
            fontSize=8,
            leading=11,
            textColor=colors.HexColor("#617083"),
            spaceAfter=6,
        ),
        "Code": ParagraphStyle(
            "Code",
            parent=base["Code"],
            fontName="Courier",
            fontSize=8,
            leading=10,
            backColor=colors.HexColor("#eef4f1"),
            borderColor=colors.HexColor("#d8dee6"),
            borderWidth=0.5,
            borderPadding=6,
            spaceAfter=8,
        ),
    }

    story = []

    story.extend(
        [
            Spacer(1, 2.2 * cm),
            p("NexusHR", styles["CoverTitle"]),
            p("AI-Enabled Enterprise HR & Workforce Intelligence Platform", styles["CoverSubtitle"]),
            p(
                "Production-grade Java full-stack HR system with employee lifecycle management, attendance, payroll, performance reviews, JWT security, PostgreSQL persistence, real-time operations readiness, and AI-style workforce insights.",
                styles["Body"],
            ),
            Spacer(1, 1.2 * cm),
            table(
                [
                    ["Prepared By", "Daksh Kumar"],
                    ["Domain", "Zidio Development - Java Full Stack"],
                    ["Date", "June 2026"],
                    ["Version", "1.0 Submission Report"],
                    ["Repository Folder", "/Users/dakshkumar/Desktop/NexusHR"],
                ],
                [5 * cm, 10.5 * cm],
            ),
            Spacer(1, 1.2 * cm),
            p(
                "Employee Lifecycle • Attendance • Payroll • Performance • AI Insights • Notifications • Monitoring",
                styles["CoverSubtitle"],
            ),
            PageBreak(),
        ]
    )

    story.extend(section_title("1. Project Overview", styles))
    story.append(
        p(
            "NexusHR is designed as a modern HR and workforce intelligence platform for mid-to-large enterprises. The platform reduces manual HR operations by centralizing employee records, attendance, leave decisions, payroll execution, performance reviews, notifications, and audit trails in one secured full-stack system.",
            styles["Body"],
        )
    )
    story.append(
        p(
            "The project follows the Zidio Java Full Stack brief by using Spring Boot for backend services, React with TypeScript for the frontend operations console, relational persistence through PostgreSQL-ready JPA entities, JWT authentication, Dockerized services, Kubernetes manifests, and monitoring support through Actuator, Prometheus, and Grafana.",
            styles["Body"],
        )
    )
    story.append(p("Target Users", styles["Heading"]))
    story.append(
        table(
            [
                ["User Type", "Primary Needs"],
                ["HR Admin", "Manage employee lifecycle, payroll, audit logs, and operational reporting."],
                ["Manager", "Review team data, approve or reject leave, inspect performance and engagement."],
                ["Employee", "Self-service login foundation for profile, payslip, leave, and notification access."],
                ["Leadership", "High-level workforce metrics, attrition risk, attendance health, and payroll readiness."],
            ],
            [4 * cm, 12 * cm],
        )
    )
    story.append(p("Business Value", styles["Heading"]))
    story.append(
        p(
            "The system improves HR efficiency by replacing spreadsheet-driven workflows with API-backed workflows and role-aware dashboards. It provides operational visibility into workforce size, attrition risk, payroll status, attendance health, and pending approvals.",
            styles["Body"],
        )
    )
    story.append(PageBreak())

    story.extend(section_title("2. Functional Requirements Coverage", styles))
    story.append(
        table(
            [
                ["ID", "Feature", "Implementation Status", "Acceptance Evidence"],
                ["F-01", "Employee Lifecycle Management", "Implemented", "JPA employee model, create/update/offboard endpoints, department mapping, seeded records."],
                ["F-02", "Attendance & Leave Management", "Implemented", "Attendance records, leave request list, approval/rejection workflow, notification creation."],
                ["F-03", "Payroll Processing", "Implemented", "Payroll run endpoint calculates gross, tax, benefits, net salary, and payslip rows."],
                ["F-04", "Performance Management", "Implemented", "Performance review model with manager score, peer score, goal completion, and rating band."],
                ["F-05", "AI Workforce Insights", "Partially Implemented", "Heuristic attrition and skill gap cards; Spring AI provider integration remains a production extension."],
                ["F-06", "Admin & Manager Dashboards", "Implemented", "React operations console with JWT login, role display, metrics, and workflow actions."],
                ["F-07", "Notification & Communication", "Partially Implemented", "Notification records generated for payroll/leave; mail/WebSocket dependencies are present for provider integration."],
            ],
            [1.4 * cm, 4.3 * cm, 3.5 * cm, 7.1 * cm],
        )
    )
    story.append(Spacer(1, 0.35 * cm))
    story.append(
        p(
            "The implementation focuses on runnable local functionality plus production-ready scaffolding. Features that require third-party credentials, such as SMS delivery and live AI model calls, are represented by clean integration points and documented extension paths.",
            styles["Body"],
        )
    )
    story.append(PageBreak())

    story.extend(section_title("3. Technology Stack", styles))
    story.append(
        table(
            [
                ["Layer", "Technology", "Purpose"],
                ["Backend", "Java 17-compatible Spring Boot 3.3", "REST APIs, validation, business workflows, security."],
                ["Frontend", "React + TypeScript + Vite", "Fast operational console with typed API integration."],
                ["Database", "PostgreSQL 17 in Docker, H2 for local dev", "ACID persistence and zero-setup development path."],
                ["Persistence", "Spring Data JPA + Hibernate", "Entity modelling and repository abstraction."],
                ["Migrations", "Flyway", "Versioned database schema management."],
                ["Security", "Spring Security + JWT + Argon2", "Role-based authorization and secure password hashing."],
                ["Cache", "Redis 7", "Session/cache readiness in Docker profile."],
                ["Monitoring", "Actuator + Prometheus + Grafana", "Health checks, metrics, and dashboard provisioning."],
                ["Deployment", "Docker, Docker Compose, Kubernetes HPA", "Containerized local and cloud-ready deployment."],
                ["CI/CD", "GitHub Actions", "Automated backend and frontend build verification."],
            ],
            [3.1 * cm, 5.2 * cm, 7.7 * cm],
        )
    )
    story.append(p("Version Note", styles["Heading"]))
    story.append(
        p(
            "The PDF brief recommends Java 21 and React 19. The local Mac environment currently has Java 17, so the implementation is Java 17-compatible while remaining Spring Boot 3.3 based. The CI file is prepared for Java 21 to align with the target production stack.",
            styles["Body"],
        )
    )
    story.append(PageBreak())

    story.extend(section_title("4. System Architecture", styles))
    story.append(p("High-Level Architecture", styles["Heading"]))
    story.append(
        p(
            "The architecture uses a React frontend communicating with a Spring Boot backend through JWT-protected REST APIs. The backend organizes controllers, services, entities, repositories, security filters, and configuration classes. Flyway owns schema creation, and Docker Compose can run PostgreSQL, Redis, Prometheus, Grafana, backend, and frontend together.",
            styles["Body"],
        )
    )
    story.append(
        p(
            "React/Vite UI<br/>"
            "&nbsp;&nbsp;| JWT REST calls<br/>"
            "Spring Boot Controllers<br/>"
            "&nbsp;&nbsp;| service orchestration<br/>"
            "Auth, HR Workflow, Payroll, Insight, Notification, Audit Services<br/>"
            "&nbsp;&nbsp;| JPA repositories<br/>"
            "PostgreSQL / H2 + Flyway schema<br/>"
            "&nbsp;&nbsp;| observability<br/>"
            "Actuator metrics -> Prometheus -> Grafana",
            styles["Code"],
        )
    )
    story.append(p("Important Backend Packages", styles["Heading"]))
    story.append(
        table(
            [
                ["Package", "Responsibility"],
                ["controller", "REST API endpoints for authentication and HR workflows."],
                ["service", "Business logic for auth, employees, leaves, payroll, insights, notifications, and audit."],
                ["model", "JPA entities mapped to Flyway schema tables."],
                ["repository", "Spring Data JPA repositories."],
                ["security", "JWT generation, parsing, authentication filter, and user details service."],
                ["config", "Security, CORS, database seeding, and application configuration."],
            ],
            [4.2 * cm, 11.8 * cm],
        )
    )
    story.append(PageBreak())

    story.extend(section_title("5. Backend Implementation", styles))
    story.append(p("Authentication and Authorization", styles["Heading"]))
    story.append(
        p(
            "NexusHR uses Spring Security with stateless JWT authentication. Demo users are seeded into the database with Argon2-hashed passwords. After login, the frontend receives a JWT containing the user role, and backend endpoints enforce access with role checks.",
            styles["Body"],
        )
    )
    story.append(
        table(
            [
                ["Endpoint", "Purpose", "Role"],
                ["POST /api/auth/login", "Login and receive JWT", "Public"],
                ["GET /api/dashboard/overview", "Executive HR metrics", "Authenticated"],
                ["POST /api/employees", "Create employee", "ADMIN/MANAGER"],
                ["DELETE /api/employees/{id}", "Offboard employee", "ADMIN"],
                ["POST /api/leaves/{id}/decision", "Approve/reject leave", "ADMIN/MANAGER"],
                ["POST /api/payroll", "Generate payroll and payslips", "ADMIN"],
                ["GET /api/audit-logs", "Sensitive audit trail", "ADMIN"],
            ],
            [4.8 * cm, 7.2 * cm, 4 * cm],
        )
    )
    story.append(p("Database Modules", styles["Heading"]))
    story.append(
        p(
            "The database schema includes departments, roles, users, employees, attendance records, leave requests, payroll runs, payslips, performance reviews, notifications, and audit logs. Flyway migration V1 creates all core tables and constraints.",
            styles["Body"],
        )
    )
    story.append(PageBreak())

    story.extend(section_title("6. Frontend Implementation", styles))
    story.append(
        p(
            "The frontend is an operations console rather than a static landing page. Users sign in with seeded demo credentials, and the app stores the JWT in local storage. If a token expires or becomes invalid, the app clears the session and returns the user to login.",
            styles["Body"],
        )
    )
    story.append(
        table(
            [
                ["UI Area", "Functionality"],
                ["Login", "Demo account login with JWT capture."],
                ["Metrics", "Workforce size, open positions, attrition risk, payroll completion."],
                ["Payroll Action", "Run payroll for a selected cycle from the admin console."],
                ["AI Insights", "Attrition, attendance, and payroll recommendations."],
                ["Employee Table", "Employee roster with department, status, and engagement score."],
                ["Leave Approvals", "Approve or reject pending leave requests."],
                ["Attendance", "Daily status and source of attendance events."],
                ["Notifications", "Generated communication queue records."],
                ["Audit Logs", "Sensitive operational action tracking."],
            ],
            [4.3 * cm, 11.7 * cm],
        )
    )
    story.append(p("Demo Credentials", styles["Heading"]))
    story.append(
        table(
            [
                ["Email", "Password", "Role"],
                ["admin@nexushr.io", "password123", "ADMIN"],
                ["manager@nexushr.io", "password123", "MANAGER"],
                ["employee@nexushr.io", "password123", "EMPLOYEE"],
            ],
            [6 * cm, 4 * cm, 4 * cm],
        )
    )
    story.append(PageBreak())

    story.extend(section_title("7. Execution Timeline", styles))
    story.append(
        table(
            [
                ["Phase", "Days", "Deliverables"],
                ["Week 1", "1-7", "Project setup, Spring Boot API, security foundation, JPA schema, Flyway, employee and attendance base modules."],
                ["Week 2", "8-14", "React TypeScript frontend, JWT login, employee dashboard, leave workflow, payroll calculation, performance reviews."],
                ["Week 3", "15-21", "AI-style insight heuristics, notification records, audit logs, manager/admin console, role-aware UI."],
                ["Week 4", "22-28", "Docker Compose, Kubernetes manifests, GitHub Actions CI, Prometheus/Grafana, README, report PDF, final QA."],
            ],
            [3 * cm, 2.2 * cm, 10.8 * cm],
        )
    )
    story.append(p("Milestones", styles["Heading"]))
    milestones = [
        "Backend build passes with Maven.",
        "Frontend production build passes with Vite and TypeScript.",
        "Login returns JWT for seeded users.",
        "Protected endpoints respond with valid JWT.",
        "Payroll endpoint creates payroll runs and payslip records.",
        "Leave decision endpoint creates notification and audit records.",
        "Single command script starts frontend and backend together.",
    ]
    for item in milestones:
        story.append(p(f"• {item}", styles["Body"]))
    story.append(PageBreak())

    story.extend(section_title("8. Security, Performance & Observability", styles))
    story.append(p("Security Highlights", styles["Heading"]))
    security_items = [
        "JWT-based stateless API authentication.",
        "Argon2 password hashing for seeded demo accounts.",
        "Role-based endpoint protection through Spring Security and method security.",
        "CORS configuration limited to local frontend origins by default.",
        "Audit log records for sensitive operations such as payroll and employee changes.",
        "No API keys or secrets committed for paid/private third-party services.",
    ]
    for item in security_items:
        story.append(p(f"• {item}", styles["Body"]))
    story.append(p("Performance & Scalability", styles["Heading"]))
    story.append(
        p(
            "The API uses stateless authentication, relational indexes through unique constraints, container-ready deployment, Kubernetes HorizontalPodAutoscaler manifests, and separation between frontend and backend services. Redis is included for cache/session readiness in the Docker profile.",
            styles["Body"],
        )
    )
    story.append(p("Observability", styles["Heading"]))
    story.append(
        p(
            "Spring Boot Actuator exposes health, metrics, and Prometheus endpoints. Docker Compose provisions Prometheus and Grafana so runtime metrics can be scraped and visualized during demos or production hardening.",
            styles["Body"],
        )
    )
    story.append(PageBreak())

    story.extend(section_title("9. Deployment & Operations", styles))
    story.append(
        table(
            [
                ["Mode", "Command / File", "Purpose"],
                ["Local H2", "./dev.sh or npm run dev", "Starts backend and frontend with in-memory H2 database."],
                ["Docker Compose", "docker compose up --build", "Runs frontend, backend, PostgreSQL, Redis, Prometheus, Grafana."],
                ["Kubernetes", "k8s/backend-deployment.yml", "Backend deployment, service, readiness/liveness probes, HPA."],
                ["Kubernetes", "k8s/frontend-deployment.yml", "Frontend deployment and service."],
                ["CI", ".github/workflows/ci.yml", "Maven test and frontend build validation."],
                ["Monitoring", "monitoring/prometheus/prometheus.yml", "Scrapes backend Actuator Prometheus endpoint."],
            ],
            [3.3 * cm, 5.4 * cm, 7.3 * cm],
        )
    )
    story.append(p("Local Run Command", styles["Heading"]))
    story.append(p("./dev.sh", styles["Code"]))
    story.append(p("Docker Run Command", styles["Heading"]))
    story.append(p("docker compose up --build", styles["Code"]))
    story.append(PageBreak())

    story.extend(section_title("10. Visual Evidence & Demo Plan", styles))
    story.append(
        p(
            "The following screenshots should be captured after running the app locally or from the public deployment URL. They can be inserted into a final presentation or demo video:",
            styles["Body"],
        )
    )
    screenshot_rows = [
        ["Screenshot", "What to Capture"],
        ["Login Screen", "JWT login page with admin demo credentials."],
        ["Dashboard Metrics", "Workforce size, attrition risk, payroll completion, open positions."],
        ["AI Insights", "Attrition, skill gap, attendance, and payroll recommendations."],
        ["Employee Roster", "Employee table with department, status, and engagement score."],
        ["Leave Workflow", "Approve/reject buttons and updated status."],
        ["Payroll Run", "Run payroll action and new payroll history entry."],
        ["Audit Logs", "Action entries after payroll and leave decisions."],
        ["Prometheus/Grafana", "Metrics endpoint and monitoring dashboard."],
    ]
    story.append(table(screenshot_rows, [4 * cm, 12 * cm]))
    story.append(p("Demo Video Flow", styles["Heading"]))
    for item in [
        "Start app using ./dev.sh.",
        "Login as admin.",
        "Show dashboard metrics and AI insights.",
        "Approve one leave request.",
        "Run payroll for a new cycle.",
        "Open notifications and audit logs.",
        "Show backend health endpoint and repository structure.",
    ]:
        story.append(p(f"• {item}", styles["Body"]))
    story.append(PageBreak())

    story.extend(section_title("11. Challenges, Learnings & Future Roadmap", styles))
    story.append(p("Challenges Solved", styles["Heading"]))
    for item in [
        "Moved from static demo data to real JPA-backed workflow data.",
        "Resolved local development friction with a single ./dev.sh runner.",
        "Handled stale JWT browser sessions by clearing invalid tokens in the frontend.",
        "Kept local development simple with H2 while preserving PostgreSQL-ready production deployment.",
    ]:
        story.append(p(f"• {item}", styles["Body"]))
    story.append(p("Key Learnings", styles["Heading"]))
    story.append(
        p(
            "The project demonstrates how enterprise systems require more than UI screens: authentication, authorization, database schema evolution, auditability, operations, deployment, and documentation all need to move together for a credible production-grade submission.",
            styles["Body"],
        )
    )
    story.append(p("Future Roadmap", styles["Heading"]))
    for item in [
        "Replace heuristic AI with Spring AI connected to OpenAI or Hugging Face.",
        "Add refresh tokens, logout token revocation, and stricter password policy.",
        "Add PDF payslip export and Excel workforce report export.",
        "Add real email/SMS provider integration.",
        "Deploy publicly on Render/Railway/AWS and record a 3-7 minute demo video.",
        "Run OWASP ZAP, Lighthouse, and load testing, then add results to this report.",
    ]:
        story.append(p(f"• {item}", styles["Body"]))

    doc.build(story, onFirstPage=header_footer, onLaterPages=header_footer)


if __name__ == "__main__":
    build()
