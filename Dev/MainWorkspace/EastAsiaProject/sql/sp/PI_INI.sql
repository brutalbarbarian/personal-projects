drop procedure if exists PI_INI;
delimiter //
create procedure PI_INI(
$ini_id int,
$inv_id int,
$wki_id int,
$ini_comments text,
$ini_quantity double,
$ini_price double(16,2)
) begin

insert TM_INI_invoice_item
(ini_id, inv_id, wki_id, ini_comments, ini_quantity, ini_price)
values
($ini_id, $inv_id, $wki_id, $ini_comments, $ini_quantity, $ini_price);

end //
delimiter ;