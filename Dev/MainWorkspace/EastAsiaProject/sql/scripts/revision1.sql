
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
doc_notes text,
-- BOPersonalDocument fields
doc_name varchar(50),
-- BOChargeableDocument fields
doc_stage int,
doc_total double(16,2),

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
	(4, 'School', 'SCH'),
	(5, 'Company', 'COM');

-- clear all the tables
drop table TR_CDT_contact_details;
drop table TM_INI_invoice_item;
drop table TM_INV_invoice;
drop table TM_WKI_work_item;
drop table TM_WRK_work;
drop table TR_STU_student;
drop table TR_SCH_school;
drop table TM_CUS_customer;
drop table TR_COM_company;

create table TR_SCH_school (
doc_id int,
constraint PK_SCH
primary key (doc_id),
constraint FK_SCH_DOC
foreign key (doc_id)
references TM_DOC_document(doc_id)
);

create table TM_CUS_customer (
doc_id int,
cus_is_student boolean,
constraint PK_CUS
primary key (doc_id),
constraint FK_CUS_DOC
foreign key(doc_id)
references TM_DOC_document(doc_id)
);

create table TM_STU_student(
doc_id int,	-- customer
doc_occurrence int,
doc_id_school int,
stu_date_start datetime,
stu_date_end datetime,
stu_notes text,
constraint PK_STU
primary key (doc_id, doc_occurrence),
constraint FK_STU_CUS
foreign key(doc_id)
references TM_CUS_customer(doc_id),
constraint FK_STU_SCH
foreign key(doc_id_school)
references TR_SCH_school(doc_id)
);

create table TR_COM_company(
doc_id int,
constraint PK_COM
primary key (doc_id),
constraint FK_COM_DOC
foreign key (doc_id)
references TM_DOC_document(doc_id)
);

create table TM_WRK_work(
doc_id int,
doc_id_customer int,
doc_id_company int,
wrk_date_required datetime,
wrk_total_paid double(16,2),
constraint PK_WRK
primary key(doc_id),
constraint FK_WRK_DOC
foreign key (doc_id)
references TM_DOC_document(doc_id),
constraint FK_WRK_CUS
foreign key (doc_id_customer)
references TM_CUS_customer(doc_id),
constraint FK_WRK_COM
foreign key (doc_id_company)
references TR_COM_company(doc_id)
);

create table TM_INV_invoice(
doc_id int,
doc_id_work int,
constraint PK_INV
primary key (doc_id),
constraint FK_INV_DOC
foreign key (doc_id)
references TM_DOC_document(doc_id),
constraint FK_INV_WRK
foreign key (doc_id_work)
references TM_WRK_work(doc_id)
);

create table TM_WKI_work_item(
doc_id int,
doc_occurrence int,
prd_id int,
wki_status int,
wki_price double(16,2),
wki_notes text,
wki_quantity double,
wki_quantity_invoiced double,
constraint PK_WKI
primary key(doc_id, doc_occurrence),
constraint FK_WKI_WRK
foreign key (doc_id)
references TM_WRK_work(doc_id),
constraint FK_WKI_PRD
foreign key (prd_id)
references TR_PRD_product(prd_id)
);

create table TM_INI_invoice_item(
doc_id int,
doc_occurrence int,
doc_occurrence_source int,
ini_price double(16,2),
ini_quantity double,
ini_notes text,
constraint PK_INI
primary key(doc_id, doc_occurrence),
constraint FK_INI_INV
foreign key (doc_id)
references TM_INV_invoice(doc_id),
constraint FK_INI_WKI
foreign key (doc_id, doc_occurrence_source)
references TM_WKI_work_item(doc_id, doc_occurrence)
);
