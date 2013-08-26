drop procedure if exists PU_INV;
delimiter //
create procedure PU_INV(
$inv_id int,
$inv_comments text,
$inv_stage int
) begin

update TM_INV_invoice 
set inv_comments = $inv_comments,
	inv_stage = $inv_stage
where inv_id = $inv_id;

end //
delimiter ;