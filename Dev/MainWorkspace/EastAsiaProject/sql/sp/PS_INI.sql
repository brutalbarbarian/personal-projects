drop procedure if exists PS_INI;
delimiter //
create procedure PS_INI(
$ini_id int
) begin

select ini_id, inv_id, wki_id, ini_comments, ini_quantity, ini_price
from TM_INI_invoice_item
where ini_id = $ini_id;

end //
delimiter ;