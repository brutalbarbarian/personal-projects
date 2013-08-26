drop procedure if exists PU_INI;
delimiter //
create procedure PU_INI(
$ini_id int,
$wki_id int,
$ini_comments text,
$ini_quantity double,
$ini_price double(16,2)
) begin

update TM_INI_invoice_item
set wki_id = $wki_id,
	ini_comments = $ini_comments,
	ini_quantity = $ini_quantity,
	ini_price = $ini_price
where ini_id = $ini_id;

end //
delimiter ;