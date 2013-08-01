drop procedure if exists PD_PRD;
delimiter //
create procedure PD_PRD(
$prd_id int
) begin

delete from TR_PRD_product
where prd_id = $prd_id;

end //
delimiter ;