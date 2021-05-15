ALTER TABLE users ADD COLUMN is_auto_registered boolean not null default false;
UPDATE users SET is_auto_registered = TRUE WHERE first_name IS NULL AND last_name IS NULL AND middle_name IS NULL AND moderator_id IS NOT NULL AND is_approved = TRUE;
