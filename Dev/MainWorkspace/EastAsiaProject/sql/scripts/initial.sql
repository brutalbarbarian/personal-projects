create table TX_PKC_primary_key_counter
(
pkc_name char(6),
pkc_id_next int,
primary key (pkc_name)
);

create table TR_USR_user
(
usr_id int,
usr_name varchar(50),
usr_password varchar(50),
usr_description text,
usr_timestamp text,
primary key (usr_id)
);

create table TR_CDT_contact_details
(
cdt_id int,
cdt_source_type int,
cdt_address_1 varchar(50),
cdt_address_2 varchar(50),
cdt_address_3 varchar(50),
cdt_city varchar(50),
cdt_country varchar(50),
cdt_postcode varchar(10),
cdt_phone varchar(50),
cdt_mobile varchar(50),
cdt_fax varchar(50),
cdt_site varchar(50),
primary key (cdt_id)
);

create table TR_SCH_school
(
sch_id int,
sch_name varchar(50),
sch_contact_name varchar(50),
sch_notes text,
primary key(sch_id)
);

create table TR_COM_company
(
com_id int,
com_name varchar(50),
primary key (com_id)
);

create table TM_CUS_customer 
(
cus_id int,
cus_name varchar(50),
cus_notes text,
cus_date_created date,
cus_is_active boolean,
cus_is_student boolean,
primary key (cus_id)
);

insert tx_pkc_primary_key_counter
(pkc_name, pkc_id_next)
values('cus_id', 0);

insert tx_pkc_primary_key_counter
(pkc_name, pkc_id_next)
values('cdt_id', 0);

insert tx_pkc_primary_key_counter
(pkc_name, pkc_id_next)
values('sch_id', 0);

insert tx_pkc_primary_key_counter
(pkc_name, pkc_id_next)
values('com_id', 0);

insert tx_pkc_primary_key_counter
(pkc_name, pkc_id_next)
values('usr_id', 0);

