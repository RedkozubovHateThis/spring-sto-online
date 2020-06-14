DELETE FROM users_user_roles WHERE user_role_id IN ( SELECT ur.id FROM user_role AS ur WHERE ur.name IN ( 'FREELANCER', 'MODERATOR', 'GUEST' ) );
DELETE FROM user_role WHERE name IN ( 'FREELANCER', 'MODERATOR', 'GUEST' );

