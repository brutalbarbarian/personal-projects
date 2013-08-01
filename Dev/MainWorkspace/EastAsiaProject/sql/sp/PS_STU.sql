drop procedure if exists PS_STU;
delimiter //
create procedure PS_STU(
$stu_id int
) begin

select stu_id, cus_id, sch_id, stu_start_date, stu_end_date, stu_notes
from TR_STU_student
where stu_id = $stu_id;

end //
delimiter ;