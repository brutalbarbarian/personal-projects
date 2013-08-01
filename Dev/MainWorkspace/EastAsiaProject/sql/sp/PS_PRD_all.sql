drop procedure if exists PS_PRD_all;
delimiter //
create procedure PS_PRD_all() 
begin

select prd_id
from TR_PRD_product;

end //
delimiter ;