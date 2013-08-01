create table TR_PRD_product
(
prd_id int,
prd_name varchar(50),
prd_price double(16,2),
prd_description text,
prd_comments text,
primary key (prd_id)
);

create table TM_WKI_work_item
(
prd_id int,
wrk_id int,
wki_id int,
wki_status int,
wki_price double(16,2),
wki_comments text,
wki_quantity int
primary key (wki_id)
);

alter table TM_WKI_work_item
add constraint FK_WKI_PRD
foreign key (prd_id)
references TR_PRD_product (prd_id);

alter table TM_WKI_work_item
add constraint FK_WKI_WRK
foreign key (wrk_id)
references TM_WRK_work (wrk_id);

insert TX_PKC_primary_key_counter
(pkc_name, pkc_id_next)
values ('prd_id', 0);

insert TX_PKC_primary_key_counter
(pkc_name, pkc_id_next)
values ('wki_id', 0);