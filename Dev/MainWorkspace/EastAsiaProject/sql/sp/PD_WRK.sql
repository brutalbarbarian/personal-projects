drop procedure if exists PD_WRK;
delimiter //
create procedure PD_WRK(
$wrk_id int
) begin

delete
from TM_WRK_work
where wrk_id = $wrk_id;

end //
delimiter ;