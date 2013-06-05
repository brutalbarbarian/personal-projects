drop procedure if exists PS_TRN_for_set;
delimiter //
create procedure PS_TRN_for_set(
	in $src_id int,
	in $date_start date,
	in $date_end date,
	in $trn_amount_min double(16,2),
	in $trn_amount_max double(16,2)
) begin

select	trn_id
  from	TM_TRN_Transactions
 where	($src_id is null or $src_id = src_id)
   and	($date_start is null or trn_date >= $date_start)
   and	($date_end is null or trn_date <= $date_end)
   and	($trn_amount_min is null or trn_amount >= $trn_amount_min)
   and	($trn_amount_max is null or trn_amount <= $trn_amount_max);

end //
delimiter ;