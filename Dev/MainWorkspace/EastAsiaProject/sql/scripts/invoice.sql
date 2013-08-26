create table TM_INV_invoice
(
inv_id int,
wrk_id int,
inv_date_created datetime,
inv_comments text,
usr_id_create int,
inv_stage int,
primary key(inv_id)
);

create table TM_INI_invoice_item
(
inv_id int,
ini_id int,
wki_id int,
ini_comments text,
ini_quantity double,
ini_price double,
primary key(ini_id)
);

alter table TM_INI_invoice_item
add constraint FK_INI_INV
foreign key (inv_id)
references TM_INV_invoice (inv_id);

alter table TM_INI_invoice_item
add constraint FK_INI_WKI
foreign key(wki_id)
references TM_WKI_work_item(wki_id);

alter table TM_INV_invoice
add constraint FK_INV_WRK
foreign key(wrk_id)
references TM_WRK_work(wrk_id);

alter table TM_INV_invoice
add constraint FK_INV_USR
foreign key(usr_id_create)
references TR_USR_user(usr_id);