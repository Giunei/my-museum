ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS username_changed_at TIMESTAMP;
