#!/bin/bash

# Скрипт для тестирования отправки события смены пароля

echo "=== Testing Kafka Message Flow ==="
echo ""

# 1. Проверяем, что все сервисы запущены
echo "1. Checking services..."
docker ps --format "table {{.Names}}\t{{.Status}}" | grep -E "account-service|notification-service|audit-service|auth-statistics-service|kafka"

echo ""
echo "2. Current topics in Kafka:"
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list

echo ""
echo "3. Checking Schema Registry schemas:"
curl -s http://localhost:9091/subjects | jq .

echo ""
echo "4. Testing password change via database direct update and event publishing..."
echo "   (This requires calling the service API or running integration test)"

# 5. Вариант через прямой вызов producer (требует создания Java класса)
# Так как нет публичного API для changePassword, создадим тестовое событие
echo ""
echo "To test the flow, we need to:"
echo "  - Create a test endpoint or"
echo "  - Run integration test that calls changePassword"
echo "  - Or manually insert event into Kafka"

echo ""
echo "=== Monitoring Consumer Logs ==="
echo "Run these commands in separate terminals:"
echo ""
echo "# Notification Service:"
echo "docker logs -f notification-service 2>&1 | grep -i password"
echo ""
echo "# Audit Service:"
echo "docker logs -f audit-service 2>&1 | grep -i password"
echo ""
echo "# Auth Statistics Service:"
echo "docker logs -f auth-statistics-service 2>&1 | grep -i password"
