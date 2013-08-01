drop procedure if exists PS_PRD;
delimiter //
create procedure PS_PRD(
$prd_id int
) begin

select prd_id, prd_name, prd_description, prd_price, prd_comments
from TR_PRD_product
where prd_id = $prd_id;

end //
delimiter ;