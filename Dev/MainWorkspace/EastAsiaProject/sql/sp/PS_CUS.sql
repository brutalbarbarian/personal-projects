drop procedure if exists PS_CUS;
delimiter //
create procedure PS_CUS(
in $cus_id int
) begin

select cus_id, cus_name_first, cus_name_last, cus_notes, 
		cus_date_created, cus_is_active, cus_is_student 
from TM_CUS_customer 
where cus_id = $cus_id;

end //
delimiter ;