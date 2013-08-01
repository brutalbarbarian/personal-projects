drop procedure if exists PU_CUS;
delimiter //
create procedure PU_CUS(
in $cus_id int,
in $cus_name varchar(50),
in $cus_notes text,
in $cus_is_active boolean,
in $cus_is_student boolean
) begin

update TM_CUS_customer
set cus_name = $cus_name, 
	cus_notes = $cus_notes, 
	cus_is_active = $cus_is_active, 
	cus_is_student = $cus_is_student
where cus_id = $cus_id;

end //
delimiter ;