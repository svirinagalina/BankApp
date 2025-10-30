
INSERT INTO user_settings (user_id, notification_enabled, language, dark_mode_enabled, created_at, updated_at)
SELECT 1, true, 'en', false, now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM user_settings WHERE user_id = 1);

