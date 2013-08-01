drop procedure if exists PI_CUS;
delimiter //
create procedure PI_CUS(
in $cus_id int,
in $cus_name varchar(50),
in $cus_notes text,
in $cus_date_created date,
in $cus_is_active boolean,
in $cus_is_student boolean
) begin

insert TM_CUS_customer
(cus_id, cus_name, cus_notes, 
cus_date_created, cus_is_active, cus_is_student)
values
($cus_id, $cus_name, $cus_notes, 
$cus_date_created, $cus_is_active, $cus_is_student);

end //
delimiter ;