drop procedure if exists PS_SCH_all;
delimiter //
create procedure PS_SCH_all(
) begin

select sch_id
from TR_SCH_school;

end //
delimiter ;