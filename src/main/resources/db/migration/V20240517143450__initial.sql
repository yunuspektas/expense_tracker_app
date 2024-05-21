create table if not exists tbl_roles
(
    id        serial primary key,
    role_type varchar(20)
);

create table if not exists tbl_users
(
    id           bigserial primary key,
    built_in     boolean,
    email        varchar(255) unique,
    name         varchar(255),
    password     varchar(255),
    phone_number varchar(255) unique,
    surname      varchar(255),
    username     varchar(255) unique,
    user_role_id int4 references tbl_roles (id)
);

create table if not exists tbl_accounts
(
    id           bigserial primary key,
    account_name varchar(255),
    account_type varchar(255),
    balance      numeric(19, 2),
    customer_id  int8 references tbl_users (id)
);


create table if not exists tbl_transactions
(
    id         bigserial primary key,
    amount     numeric(19, 2),
    category   varchar(255),
    date       date,
    type       int4,
    account_id int8 references tbl_accounts (id)
);