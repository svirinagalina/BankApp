INSERT INTO audit_entries (timestamp, event_type, message, user_id)
SELECT '2025-01-01T00:00:00Z', 'событие', 'сообщение', '1'
WHERE NOT EXISTS (SELECT 1 FROM audit_entries WHERE id = 1);

