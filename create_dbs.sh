#!/usr/bin/env bash
set -Eeuo pipefail

# Какие БД хотим иметь в основном postgres-контейнере
DATABASES=(
  "notifications_db"
  "security_db"
  "mainDB"
  "kyc_verifications"
  "audit_db"
)

OWNER="${POSTGRES_USER:-root}"

log() { echo "[$(date +'%Y-%m-%d %H:%M:%S')] $*"; }

ensure_db() {
  local db="$1"
  local owner="$2"

  # Проверяем существование
  if psql -v ON_ERROR_STOP=1 -U "$owner" -d postgres -qAt \
      -c "SELECT 1 FROM pg_database WHERE datname = '$db';" | grep -qx '1'; then
    log "ℹ️  Database '$db' already exists — skip."
    return 0
  fi

  # Создаём БД c корректной кодировкой
  log "🛠  Creating database '$db' (OWNER: $owner)..."
  psql -v ON_ERROR_STOP=1 -U "$owner" -d postgres -qAt <<-EOSQL
    CREATE DATABASE "$db" WITH TEMPLATE template0 ENCODING 'UTF8';
    ALTER DATABASE "$db" OWNER TO "$owner";
EOSQL
  log "✅ Database '$db' created."
}

log "===> Ensuring required databases exist (owner: $OWNER) ..."
for db in "${DATABASES[@]}"; do
  ensure_db "$db" "$OWNER"
done
log "🎉 Done."
