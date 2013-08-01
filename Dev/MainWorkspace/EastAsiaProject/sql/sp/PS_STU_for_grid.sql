drop procedure if exists PS_STU_for_grid;
delimiter //
create procedure PS_STU_for_grid(
$cus_id int,
$sch_id int,
$stu_start_date date,
$stu_end_date date
) begin

select stu_id
from TR_STU_student
where ($cus_id is null or $cus_id = $cus_id)
and ($sch_id is null or $sch_id = $sch_id)
and ($stu_start_date is null or $stu_start_date <= stu_start_date)
and ($stu_end_date is null or $stu_end_date >= stu_end_date);

end //
delimiter ;