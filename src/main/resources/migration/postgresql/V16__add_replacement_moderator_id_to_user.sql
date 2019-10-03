ALTER TABLE users ADD COLUMN in_vacation boolean not null default false;
ALTER TABLE users ADD COLUMN replacement_moderator_id bigint;
ALTER TABLE users ADD FOREIGN KEY (replacement_moderator_id) REFERENCES users (id);
CREATE INDEX users_replacement_moderator_id_idx ON users (replacement_moderator_id);
CREATE INDEX users_in_vacation_idx ON users (in_vacation);
