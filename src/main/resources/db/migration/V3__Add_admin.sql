create extension if not exists pgcrypto;

-- Insert here your credentials
INSERT INTO user_entity (username, password)
VALUES (${admin_username}, crypt(${admin_password}, gen_salt('bf', 8)));


INSERT INTO user_authority (user_id, authority_id)
SELECT usr.id, auth.authority_id
FROM user_entity as usr
         CROSS JOIN authority as auth;