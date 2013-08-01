drop procedure if exists PI_STU;
delimiter //
create procedure PI_STU(
$stu_id int,
$cus_id int,
$sch_id int,
$stu_start_date date,
$stu_end_date date,
$stu_notes text
) begin

insert TR_STU_student
(stu_id, cus_id, sch_id, stu_start_date, stu_end_date, stu_notes)
values
(
$stu_id,
$cus_id,
$sch_id,
$stu_start_date,
$stu_end_date,
$stu_notes
);

end //
delimiter ;