create table TX_PKC_primary_key_counter
(
pkc_name char(6),
pkc_id_next int,
primary key (pkc_name)
);

insert TX_PKC_primary_key_counter
(pkc_name, pkc_id_next)
values
('src_id', 0);

insert TX_PKC_primary_key_counter
(pkc_name, pkc_id_next)
values
('trn_id', 0);

create table TR_SRC_source
(
src_id int,
src_name varchar(50),
primary key (src_id)
);

create table TM_TRN_transactions
(
trn_id int,
trn_amount double(16, 2),
src_id int,
trn_notes text,
trn_date date,
primary key (trn_id)
);

alter table TM_TRN_transactions
add constraint fk_transaction_source
foreign key (src_id)
references TR_SRC_source (src_id);