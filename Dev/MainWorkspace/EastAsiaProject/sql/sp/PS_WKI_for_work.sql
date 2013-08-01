drop procedure if exists PS_WKI_for_work;
delimiter //
create procedure PS_WKI_for_work(
$wrk_id int
) begin

select wki_id
from TM_WKI_work_item
where wrk_id = $wrk_id;

end //
delimiter ;