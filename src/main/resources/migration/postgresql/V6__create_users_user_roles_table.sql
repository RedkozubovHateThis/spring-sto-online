CREATE TABLE users_user_roles
(
    user_id bigint NOT NULL,
    user_role_id bigint NOT NULL,
    CONSTRAINT users_user_roles_pkey PRIMARY KEY (user_id, user_role_id)
);

ALTER TABLE users_user_roles ADD FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE users_user_roles ADD FOREIGN KEY (user_role_id) REFERENCES user_role (id);