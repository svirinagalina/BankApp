#!/bin/bash
set -e

echo "⏳ Waiting for Postgres to start..."
until pg_isready -h localhost -U "$POSTGRES_USER" > /dev/null 2>&1; do
  sleep 1
done
echo "✅ Postgres is ready."

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

echo "🧩 Checking and creating databases..."
for db in "${DATABASES[@]}"; do
  if ! psql -U "$POSTGRES_USER" -tc "SELECT 1 FROM pg_database WHERE datname = '$db'" | grep -q 1; then
    echo "📗 Creating database: $db"
    psql -U "$POSTGRES_USER" -c "CREATE DATABASE $db"
  else
    echo "✅ Database $db already exists."
  fi
done

echo "✅ All databases verified or created."

# --- MinIO setup ---
echo "⏳ Waiting for MinIO to be reachable..."
MINIO_ENDPOINT=${MINIO_ENDPOINT:-http://minio:9000}
until curl -s "$MINIO_ENDPOINT/minio/health/live" > /dev/null; do
  sleep 2
done
echo "✅ MinIO is reachable."

echo "🪣 Configuring MinIO bucket..."
apk add --no-cache curl >/dev/null 2>&1 || true
wget -q https://dl.min.io/client/mc/release/linux-amd64/mc -O /usr/local/bin/mc
chmod +x /usr/local/bin/mc

export MC_HOST_local="$MINIO_ENDPOINT"
MC_ALIAS="local"
MC_USER="${MINIO_ROOT_USER:-minioadmin}"
MC_PASS="${MINIO_ROOT_PASSWORD:-minioadmin}"

mc alias set $MC_ALIAS $MINIO_ENDPOINT $MC_USER $MC_PASS >/dev/null

BUCKET_NAME=${MINIO_BUCKET:-kyc-files}

if ! mc ls $MC_ALIAS/$BUCKET_NAME >/dev/null 2>&1; then
  mc mb $MC_ALIAS/$BUCKET_NAME
  echo "✅ MinIO bucket '$BUCKET_NAME' created."
else
  echo "✅ MinIO bucket '$BUCKET_NAME' already exists."
fi

echo "🎉 Initialization complete!"