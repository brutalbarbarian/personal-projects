drop procedure if exists PS_WRK;
delimiter //
create procedure PS_WRK(
$wrk_id int
) begin

select wrk_id, cus_id, com_id, usr_id_created, wrk_date_create,
	wrk_date_due, wrk_notes, wrk_stage
from TM_WRK_work
where wrk_id = $wrk_id;

end //
delimiter ;