INSERT INTO users(username, password, account_expired, account_locked, credentials_expired, enabled)
  VALUES ('admin', /*admin1234*/'$2a$08$qvrzQZ7jJ7oy2p/msL4M0.l83Cd0jNsX6AJUitbgRXGzge4j035ha', FALSE, FALSE, FALSE, TRUE);

INSERT INTO users_user_roles(user_id, user_role_id) VALUES (
(SELECT u.id FROM users AS u WHERE u.username = 'admin')
,
(SELECT ur.id FROM user_role AS ur WHERE ur.name = 'ADMIN')
);