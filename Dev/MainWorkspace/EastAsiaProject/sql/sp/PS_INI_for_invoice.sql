drop procedure if exists PS_INI_for_invoice;
delimiter //
create procedure PS_INI_for_invoice(
$inv_id int
) begin

select ini_id
from TM_INI_invoice_item
where inv_id = $inv_id;

end //
delimiter ;