alter table if exists tbl_users
    add column role varchar(20);

update tbl_users u
set role = (select r.role_type from tbl_roles r where r.id = u.user_role_id )
where role is null;

-- if application deployment runs on multiple server instances or there are other
-- apps using tbl_roles table then below sql code should be added after this code
-- deployed on all app instances and all other apps using tbl_roles are refactored
-- accordingly. Otherwise, we will crash other app instances and other applications.

-- For this migration, I assume this app runs on a single instance and there is no other
-- app using tbl_roles table.

alter table tbl_users
    drop constraint tbl_users_user_role_id_fkey;

alter table tbl_users
    drop column user_role_id;

drop table tbl_roles;

