drop procedure if exists PD_TRN;
delimiter //
create procedure PD_TRN(
	in $trn_id int
) begin

delete from TM_TRN_transactions 
where trn_id = $trn_id;

end //
delimiter ;