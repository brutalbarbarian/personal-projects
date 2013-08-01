drop procedure if exists PU_WRK;
delimiter //
create procedure PU_WRK(
$wrk_id int,
$cus_id int,
$com_id int,
$usr_id_created int,
$wrk_date_create date,
$wrk_date_due date,
$wrk_notes text,
$wrk_stage int
) begin

update TM_WRK_work
set wrk_id = $wrk_id,
	cus_id = $cus_id,
	com_id = $com_id,
	usr_id_created = $usr_id_created,
	wrk_date_create = $wrk_date_create,
	wrk_date_due = $wrk_date_due,
	wrk_notes = $wrk_notes,
	wrk_stage = $wrk_stage;

end //
delimiter ;