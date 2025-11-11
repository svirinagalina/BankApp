#!/usr/bin/env bash
set -Eeuo pipefail

if [ -f ".env" ]; then
  set -a; source .env; set +a
fi

PGUSER="${POSTGRES_USER:-postgres}"
PGPASSWORD="${POSTGRES_PASSWORD:-postgres}"
PGHOST="${POSTGRES_HOST:-localhost}"
PGPORT="${POSTGRES_PORT:-5432}"

export PGPASSWORD

echo "🧰 Ensuring databases on ${PGHOST}:${PGPORT} as ${PGUSER} ..."

for i in {1..60}; do
  if psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d postgres -Atqc 'SELECT 1' >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

databases=(
  "notifications_db"
  "security_db"
  "mainDB"
  "kyc_verifications"
  "audit_db"
  "account_db"
  "settings_db"
  "auth_stats_db"
)

for db in "${databases[@]}"; do
  echo "→ ensuring database: $db"
  psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d postgres -v ON_ERROR_STOP=1 -c \
    "SELECT 'CREATE DATABASE \"${db}\"' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname='${db}')\gexec"
done

echo "✅ All databases ensured."
