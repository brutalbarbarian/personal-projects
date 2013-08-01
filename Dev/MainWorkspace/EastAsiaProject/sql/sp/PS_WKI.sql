drop procedure if exists PS_WKI;
delimiter //
create procedure PS_WKI(
$wki_id int
) begin

select wki_id, wrk_id, prd_id, wki_status, wki_price, wki_comments, wki_quantity,
from TM_WKI_work_item
where wki_id = $wki_id;

end //
delimiter ;