drop procedure if exists PS_SCH;
delimiter //
create procedure PS_SCH(
in $sch_id int
) begin

select sch_id, sch_name, sch_contact_name, sch_notes 
from TR_SCH_school 
where sch_id = $sch_id;

end //
delimiter ;