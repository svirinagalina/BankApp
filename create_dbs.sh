#!/bin/bash
set -euo pipefail

echo "▶ Ensuring databases via local UNIX socket..."

for i in {1..60}; do
  if psql -U "$POSTGRES_USER" -d postgres -Atqc 'SELECT 1' >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

DATABASES=(
  "notifications_db"
  "security_db"
  "mainDB"
  "kyc_verifications"
  "audit_db"
  "account_db"
  "settings_db"
  "auth_stats_db"
)

for db in "${DATABASES[@]}"; do
  echo "→ ensuring database: ${db}"
  psql -U "$POSTGRES_USER" -d postgres -v ON_ERROR_STOP=1 -c \
  "SELECT 'CREATE DATABASE ${db}' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname='${db}')\gexec"
done

echo "✅ All databases ensured."