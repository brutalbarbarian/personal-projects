drop procedure if exists PD_SCH;
delimiter //
create procedure PD_SCH(
in $sch_id int
) begin

delete from TR_SCH_school 
where sch_id = $sch_id;

end //
delimiter ;