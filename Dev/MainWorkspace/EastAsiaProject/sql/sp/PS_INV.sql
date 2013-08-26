drop procedure if exists PS_INV;
delimiter //
create procedure PS_INV(
$inv_id int
) begin

select inv_id, wrk_id, inv_date_created, inv_comments, usr_id_create, inv_stage
from TM_INV_invoice
where inv_id = $inv_id;

end //
delimiter ;