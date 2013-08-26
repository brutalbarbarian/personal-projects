drop procedure if exists PD_INI;
delimiter //
create procedure PD_INI(
$ini_id int
) begin

delete from TM_INI_invoice_item
where ini_id = $ini_id;

end //
delimiter ;