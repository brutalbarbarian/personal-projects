create table TM_WRK_work
(
wrk_id int,
cus_id int,
com_id int,
usr_id_created int,
wrk_date_create date,
wrk_date_due date,
wrk_notes text,
wrk_stage int,
primary key(wrk_id)
);

alter table TM_WRK_work
add constraint FK_WRK_COM
foreign key(com_id)
references TR_COM_company(com_id);

alter table TM_WRK_work
add constraint FK_WRK_CUS
foreign key(cus_id)
references TM_CUS_customer(cus_id);

alter table TM_WRK_work
add constraint FK_WRK_USR
foreign key(usr_id_created)
references TR_USR_user(usr_id);