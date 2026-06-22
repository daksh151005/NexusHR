# NexusHR Deployment Guide

## Fastest Local Production Test

```bash
docker compose up --build
```

Open:

```text
http://localhost:5173/
```

## Recommended Public Deployment

Deploy as three services:

- PostgreSQL database
- Spring Boot backend
- React frontend

## Backend Environment Variables

```bash
SPRING_PROFILES_ACTIVE=postgres
DB_URL=jdbc:postgresql://<host>:5432/<database>
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>
JWT_SECRET=<long-random-secret-at-least-32-characters>
CORS_ALLOWED_ORIGINS=https://<frontend-domain>
```

Backend health check:

```text
/actuator/health
```

## Frontend Environment Variable

```bash
VITE_API_URL=https://<backend-domain>/api
```

This must be set during the frontend build.

## Option A: Render/Railway/Fly Style Deployment

1. Create a managed PostgreSQL database.
2. Deploy `backend/` as a Docker web service.
3. Add the backend environment variables listed above.
4. Deploy `frontend/` as a Docker static web service.
5. Build the frontend with `VITE_API_URL=https://<backend-domain>/api`.
6. Set backend `CORS_ALLOWED_ORIGINS=https://<frontend-domain>`.

## Option B: VPS Deployment With Docker Compose

1. Copy the project to a VPS.
2. Create a `.env` file from `.env.example`.
3. Update `docker-compose.yml` secrets/passwords for production.
4. Run:

```bash
docker compose up --build -d
```

5. Put Nginx or Caddy in front for HTTPS.

## Option C: Kubernetes

1. Build and push backend/frontend images.
2. Create `nexushr-secrets` with database and JWT values.
3. Apply manifests:

```bash
kubectl apply -f k8s/
```

4. Attach an ingress controller and TLS certificate.
