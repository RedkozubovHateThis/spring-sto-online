ALTER TABLE users ADD COLUMN IF NOT EXISTS moderator_id BIGINT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_approved BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD FOREIGN KEY (moderator_id) REFERENCES users (id);
