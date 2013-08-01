drop procedure if exists PD_STU;
delimiter //
create procedure PD_STU(
$stu_id int
) begin

delete from TR_STU_student
where stu_id = $stu_id;

end //
delimiter ;