drop procedure if exists PS_TRN;
delimiter //
create procedure PS_TRN(
	in $trn_id int
) begin

select * from TM_TRN_transactions trn
where trn.trn_id = $trn_id;

end //
delimiter ;

