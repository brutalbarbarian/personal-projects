drop procedure if exists PU_SCH;
delimiter //
create procedure PU_SCH(
in $sch_id int,
in $sch_name varchar(50),
in $sch_contact_name varchar(50),
in $sch_notes text
) begin

update TR_SCH_school 
set sch_id = $sch_id,
	sch_contact_name = $sch_contact_name,
	sch_notes = $sch_notes
where sch_id = $sch_id;

end //
delimiter ;
