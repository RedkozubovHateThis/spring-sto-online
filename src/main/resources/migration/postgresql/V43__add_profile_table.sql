CREATE SEQUENCE profile_id_seq;
CREATE TABLE profile
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('profile_id_seq'::regclass),
    deleted boolean not null default false,
    name character varying (255),
    address character varying (255),
    email character varying (255),
    phone character varying (255),
    inn character varying (255),
    user_id bigint
);
ALTER TABLE users ADD COLUMN profile_id bigint;
ALTER TABLE users ADD FOREIGN KEY (profile_id) REFERENCES profile(id);
do $$
    declare
        auser record;
        aprofile record;
    begin
        for auser in
            select u.* from users as u
            loop
                insert into profile (deleted, name, address, email, phone, inn, user_id) values
               (
                false, auser.last_name || ' ' || auser.first_name || ' ' || auser.middle_name, '', auser.email, auser.phone, auser.inn, auser.id
               );
            end loop;
        for aprofile in
            select p.* from profile as p
            loop
                update users set profile_id = aprofile.id where id = aprofile.user_id;
            end loop;
    end;
$$;
ALTER TABLE profile DROP COLUMN user_id;
