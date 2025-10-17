#!/bin/bash
set -e

echo "Checking existing databases..."
psql -U "$POSTGRES_USER" -tc "SELECT 1 FROM pg_database WHERE datname = 'notifications_db'" | grep -q 1 || \
    psql -U "$POSTGRES_USER" -c "CREATE DATABASE notifications_db"

echo "✅ Database notifications_db created (if it didn't exist)."
