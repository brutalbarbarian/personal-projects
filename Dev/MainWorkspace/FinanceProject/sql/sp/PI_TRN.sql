drop procedure if exists PI_TRN;
delimiter //
create procedure PI_TRN(
	in $trn_id int,
	in $trn_amount double(16, 2),
	in $src_id int,
	in $trn_notes text,
	in $trn_date date
) begin

insert TM_TRN_transactions
(trn_id, trn_amount, src_id, trn_notes, trn_date)
values ($trn_id, $trn_amount, $src_id, $trn_notes, $trn_date);

end //
delimiter ;

