drop procedure if exists PD_INV;
delimiter //
create procedure PD_INV(
$inv_id int
) begin

delete from TM_INV_invoice
where inv_id = $inv_id;

end //
delimiter ;