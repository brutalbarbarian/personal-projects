drop procedure if exists PI_SCH;
delimiter //
create procedure PI_SCH(
in $sch_id int,
in $sch_name varchar(50),
in $sch_contact_name varchar(50),
in $sch_notes text
) begin

insert into TR_SCH_school 
(sch_id, sch_name, sch_contact_name, sch_notes) 
values ($sch_id, $sch_name, $sch_contact_name, $sch_notes);

end //
delimiter ;
