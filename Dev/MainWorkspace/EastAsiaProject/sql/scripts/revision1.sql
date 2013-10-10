
create table TX_DTY_document_type (
dty_id int,
dty_name varchar(50),
dty_code varchar(3),
constraint PK_DTY
primary key (dty_id)
);

create table TM_DOC_document(
doc_id int,
dty_id int,
doc_date_created datetime,
usr_id_created int,
doc_date_changed datetime,
usr_id_changed int,
usr_id_owner int,
constraint PK_DOC
primary key(doc_id),

constraint FK_DOC_DTY
foreign key (dty_id)
references TX_DTY_document_type(dty_id),

constraint FK_DOC_USR_created
foreign key (usr_id_created)
references TR_USR_user(usr_id),

constraint FK_DOC_USR_changed
foreign key (usr_id_changed)
references TR_USR_user(usr_id),

constraint FK_DOC_USR_owner
foreign key (usr_id_owner)
references TR_USR_user(usr_id)
);

create table TR_ADR_address (
doc_id int,
doc_occurrence int,
adr_address1 varchar(50),
adr_address2 varchar(50),
adr_city varchar(50),
adr_postcode varchar(10),
constraint PK_ADR
primary key (doc_id, doc_occurrence),

constraint FK_ADR_DOC
foreign key (doc_id)
references TM_DOC_document(doc_id)
);

create table TR_CON_contact (
doc_id int,
doc_occurrence int,
con_name varchar(50),
con_work varchar(50),
con_home varchar(50),
con_mobile varchar(50),
con_email varchar(50),
constraint PK_CON
primary key(doc_id, doc_occurrence),

constraint FK_CON_DOC
foreign key (doc_id)
references TM_DOC_document(doc_id)
);

insert TX_DTY_document_type(dty_id, dty_name, dty_code)
values 
	(1, 'Customer', 'CUS'),
	(2, 'Work', 'WRK'),
	(3, 'Invoice', 'INV'),
	(4, 'School', 'SCH');

delete from TR_CDT_contact_details;
delete from TM_INI_invoice_item;
delete from TM_INV_invoice;
delete from TM_WKI_work_item;
delete from TM_WRK_work;
delete from TR_STU_student;
delete from TR_SCH_school;
delete from TM_CUS_customer;

