create table TR_STU_student
(
stu_id int,
cus_id int,
sch_id int,
stu_start_date date,
stu_end_date date,
stu_notes text,
primary key(stu_id)
);

insert TX_PKC_primary_key_counter
(pkc_name, pkc_id_next)
values('stu_id', 0);

alter table TR_STU_student
add constraint fk_stu_cus
foreign key(cus_id)
references TM_CUS_customer(cus_id);

alter table TR_STU_student
add constraint fk_stu_sch
foreign key(sch_id)
references TR_SCH_school(sch_id);
