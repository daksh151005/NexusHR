#!/usr/bin/env sh
set -eu

BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"
BACKEND_URL="http://localhost:${BACKEND_PORT}/actuator/health"

echo "Starting NexusHR backend on http://localhost:${BACKEND_PORT}"
(cd backend && mvn -q spring-boot:run -Dspring-boot.run.arguments="--server.port=${BACKEND_PORT}" ) &
BACKEND_PID=$!

echo "Starting NexusHR frontend on http://localhost:${FRONTEND_PORT}"
(cd frontend && npm run dev -- --host 0.0.0.0 --port "${FRONTEND_PORT}") &
FRONTEND_PID=$!

# Wait for backend to accept HTTP (health can be UP/DOWN depending on external deps)
# Use curl if present; fallback to wget if needed.
wait_for_backend() {
  i=0
  max="${1:-90}"
  while [ "$i" -lt "$max" ]; do
    if command -v curl >/dev/null 2>&1; then
      if curl -fsS "${BACKEND_URL}" >/dev/null 2>&1; then
        return 0
      fi
    else
      # Busybox wget style; not guaranteed on all environments.
      if command -v wget >/dev/null 2>&1; then
        if wget -qO- "${BACKEND_URL}" >/dev/null 2>&1; then
          return 0
        fi
      fi
    fi
    i=$((i+1))
    sleep 1
  done
  return 1
}

echo "Waiting for backend at ${BACKEND_URL} ..."
if ! wait_for_backend 90; then
  echo "Backend did not become reachable in time: ${BACKEND_URL}"
  echo "Stopping processes..."
  kill "${BACKEND_PID}" "${FRONTEND_PID}" >/dev/null 2>&1 || true
  exit 1
fi

echo "Backend reachable. Keeping dev servers running."
# Wait for either process to exit
wait -n "${BACKEND_PID}" "${FRONTEND_PID}"
