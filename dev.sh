#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT_DIR/.logs"
BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"

mkdir -p "$LOG_DIR"

is_port_busy() {
  lsof -nP -iTCP:"$1" -sTCP:LISTEN >/dev/null 2>&1
}

wait_for_url() {
  local name="$1"
  local url="$2"
  local attempts="${3:-45}"

  for _ in $(seq 1 "$attempts"); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "$name is ready: $url"
      return 0
    fi
    sleep 1
  done

  echo "$name did not become ready in time. Check logs in $LOG_DIR." >&2
  return 1
}

cleanup() {
  echo
  echo "Stopping NexusHR..."
  if [[ -n "${BACKEND_PID:-}" ]] && kill -0 "$BACKEND_PID" >/dev/null 2>&1; then
    kill "$BACKEND_PID" >/dev/null 2>&1 || true
  fi
  if [[ -n "${FRONTEND_PID:-}" ]] && kill -0 "$FRONTEND_PID" >/dev/null 2>&1; then
    kill "$FRONTEND_PID" >/dev/null 2>&1 || true
  fi
}

trap cleanup EXIT INT TERM

if is_port_busy "$BACKEND_PORT"; then
  echo "Port $BACKEND_PORT is already in use. Stop the existing backend or run with BACKEND_PORT=8081 ./dev.sh" >&2
  exit 1
fi

if is_port_busy "$FRONTEND_PORT"; then
  echo "Port $FRONTEND_PORT is already in use. Stop the existing frontend or run with FRONTEND_PORT=5174 ./dev.sh" >&2
  exit 1
fi

echo "Starting NexusHR backend on http://localhost:$BACKEND_PORT"
(
  cd "$ROOT_DIR/backend"
  mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=$BACKEND_PORT"
) >"$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID=$!

echo "Starting NexusHR frontend on http://localhost:$FRONTEND_PORT"
(
  cd "$ROOT_DIR/frontend"
  if [[ ! -d node_modules ]]; then
    npm install
  fi
  npm run dev -- --host 0.0.0.0 --port "$FRONTEND_PORT"
) >"$LOG_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!

# In Vercel build/runtime, some health indicators (e.g., SMTP) may be unavailable,
# causing actuator health to be DOWN/slow even though the app is already running.
# So we only require that the actuator endpoint responds successfully.
wait_for_url "Backend" "http://localhost:$BACKEND_PORT/actuator/health" 90
wait_for_url "Frontend" "http://localhost:$FRONTEND_PORT/"

echo
echo "NexusHR is running:"
echo "  Frontend: http://localhost:$FRONTEND_PORT/"
echo "  Backend:  http://localhost:$BACKEND_PORT"
echo "  Logs:     $LOG_DIR"
echo
echo "Press Ctrl+C to stop both servers."

wait
