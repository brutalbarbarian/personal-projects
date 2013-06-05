drop procedure if exists PU_TRN;
delimiter //
create procedure PU_TRN(
	in $trn_id int,
	in $trn_amount double(16, 2),
	in $src_id int,
	in $trn_notes text,
	in $trn_date date
) begin

update TM_TRN_transactions
set	trn_amount = $trn_amount,
	src_id = $src_id,
	trn_notes = $trn_notes,
	trn_date = $trn_date
where trn_id = $trn_id;

end //
delimiter ;

