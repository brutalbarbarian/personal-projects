drop procedure if exists PU_STU;
delimiter //
create procedure PU_STU(
$stu_id int,
$cus_id int,
$sch_id int,
$stu_start_date date,
$stu_end_date date,
$stu_notes text
) begin

update TR_STU_student
set cus_id = $cus_id,
	sch_id = $sch_id,
	stu_start_date = $stu_start_date,
	stu_end_date = $stu_end_date,
	stu_notes = $stu_notes
where stu_id = @stu_id;

end //
delimiter ;