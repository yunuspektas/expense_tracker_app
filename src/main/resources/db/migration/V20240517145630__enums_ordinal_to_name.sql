alter table tbl_transactions
    add column type_temp varchar(20);

update tbl_transactions
set type_temp = case when type = 0 then 'DEPOSIT' else 'WITHDRAWAL' end
where type_temp is null;

alter table tbl_transactions
    drop column type;

alter table tbl_transactions
    rename column type_temp to type;